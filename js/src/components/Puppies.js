import React from 'react';
import { Query, Mutation } from "react-apollo";
import gql from "graphql-tag";
import { StyleSheet, css } from 'aphrodite';

const GQLListPuppies = gql`
{
    puppies {
        id,
        name,
        url
    }
}`;

const GQLAddPuppy = gql`
mutation AddPuppy($name: String!, $url: String!) {
    addPuppy(name: $name, url: $url) {
        id
        __typename
        name
        url
    }
}`;

const GQLDeletePuppy = gql`
mutation DeletePuppy($id: String!) {
    deletePuppy(id: $id) {
        id
    }
}`;


function updateStore(proxy, response) {
    const qData = proxy.readQuery({ query: GQLListPuppies });

    if (response.data.addPuppy) {
        qData.puppies.push(response.data.addPuppy);
    } else if (response.data.deletePuppy) {
        qData.puppies = qData.puppies.filter(
            puppy => puppy.id !== response.data.deletePuppy.id);
    }

    proxy.writeQuery({ query: GQLListPuppies, data: qData });
}

class Puppy extends React.Component {
    render() {
        const {puppy} = this.props;
        return <Mutation
            mutation={GQLDeletePuppy}
            update={updateStore}
        >
            {deletePuppy => (
                <div
                    className={css(styles.puppyItem)}
                    key={puppy.id}
                >
                    <div className={css(styles.puppyImage)}>
                        <img src={puppy.url} height={200} />
                        {(puppy.id.substr(0, 7) !== "__NEW__") &&
                            <button
                                className={css(styles.deletePuppy)}
                                onClick={() => deletePuppy({
                                    variables: {id: puppy.id},
                                    optimisticResponse: {
                                        __typename: "Mutation",
                                        deletePuppy: {
                                            __typename: "Puppy",
                                            id: puppy.id,
                                        },
                                    },
                                })}
                            >
                                ❌
                            </button>}
                    </div>
                    <label className={css(styles.puppyLabel)}>
                        {puppy.name}
                        {(puppy.id.substr(0, 7) === "__NEW__") &&
                            <img
                                height={16}
                                src="/images/saving.gif"
                                width={16}
                            />}
                    </label>
                </div>)}
        </Mutation>;
    }
}

class AddPuppyForm extends React.Component {
    render() {
        const {
            name, url, onUpdatePuppyName, onUpdatePuppyUrl, addPuppy,
        } = this.props;

        return <Mutation
            mutation={GQLAddPuppy}
            variables={{ name: this.props.name, url: this.props.url }}
            optimisticResponse={{
                __typename: "Mutation",
                addPuppy: {
                    __typename: "Puppy",
                    id: "__NEW__" + this.props.url,
                    name: this.props.name,
                    url: this.props.url,
                },
            }}
            update={updateStore}
        >
            {addPuppy => (
                <div className={css(styles.newPuppyForm)}>
                <label>Name</label>
                <input
                    onChange={evt => onUpdatePuppyName(evt.target.value)}
                    value={name}
                />
                <label>URL</label>
                <input
                    onChange={evt => onUpdatePuppyUrl(evt.target.value)}
                    value={url}
                />
                <button onClick={() => {
                    addPuppy();
                    onUpdatePuppyName("");
                    onUpdatePuppyUrl("");
                }}>
                    Add Puppy 
                </button>
            </div>
            )}
        </Mutation>;
    }
}

export default class Puppies extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            name: "",
            url: "",
        };
    }

    updatePuppyName(name) {
        this.setState({ name });
    }

    updatePuppyUrl(url) {
        this.setState({ url });
    }

    render() {
        return <Query query={GQLListPuppies}>
        {({ loading, error, data: qData }) => {
            if (loading) return <p>Loading...</p>;
            if (error) return <p>Error!</p>;

            return <div className={css(styles.puppyList)}>
                {qData.puppies.map(puppy => <Puppy
                    key={puppy.id}
                    onDeletePuppy={() => deletePuppy()}
                    puppy={puppy}
                />)}
                <AddPuppyForm
                    key="AddForm"
                    onUpdatePuppyName={this.updatePuppyName.bind(this)}
                    onUpdatePuppyUrl={this.updatePuppyUrl.bind(this)}
                    name={this.state.name}
                    url={this.state.url}
                />
            </div>;
        }}
        </Query>
    }
}

const styles = StyleSheet.create({
    deletePuppy: {
        background: "rgba(255, 255, 255, 0.5)",
        border: "none",
        borderRadius: 8,
        height: 24,
        position: "absolute",
        right: 0,
        top: 0,
        width: 24,
    },

    newPuppyForm: {
        display: "flex",
        flexDirection: "column",
    },

    puppyList: {
        display: "flex",
        flexWrap: "wrap",
        margin: "0 auto",
    },

    puppyImage: {
        position: "relative",
    },

    puppyItem: {
        display: "flex",
        flexDirection: "column",
        padding: 10,
        height: 300,
    },

    puppyLabel: {
        fontWeight: "bold",
    },
});