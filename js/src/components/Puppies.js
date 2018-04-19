import React from 'react';
import { Query } from "react-apollo";
import gql from "graphql-tag";
import { StyleSheet, css } from 'aphrodite';

export default class Puppies extends React.Component {
    render() {
        return <Query query={gql`
            {
                puppies {
                    id,
                    name,
                    url
                }
            }
        `}>
        {({ loading, error, data }) => {
            if (loading) return <p>Loading...</p>;
            if (error) return <p>Error!</p>;

            return <div className={css(styles.puppyList)}>
                {data.puppies.map(puppy => <div
                    className={css(styles.puppyItem)}
                    key={puppy.id}
                >
                    <img src={puppy.url} height={200} />
                    <label className={css(styles.puppyLabel)}>
                        {puppy.name}
                    </label>
                </div>)}
            </div>;
        }}
        </Query>
    }
}

const styles = StyleSheet.create({
    puppyList: {
        display: "flex",
        margin: "0 auto",
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