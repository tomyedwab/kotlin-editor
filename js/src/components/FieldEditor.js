import React from 'react';
import { Query, Mutation } from "react-apollo";
import gql from "graphql-tag";
import { debounce, capitalize } from "../util.js";

export default class FieldEditor extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            edits: null,
        };

        this.sendMutation = debounce(applyMutation => {
            if (this.state.edits === this.props.revision.content[this.props.field]) {
                // Nothing to do
                return;
            }
            const variables = {
                id: this.props.revision.id,
            };
            variables[this.props.field] = this.state.edits;

            const optimisticResponse = {
                __typename: "Mutation",
            };
            const mutationName = `set${this.props.kind}${capitalize(this.props.field)}`;
            optimisticResponse[mutationName] = {
                __typename: `${this.props.kind}Revision`,
                id: this.props.revision.id,
                sha: "SAVING...",
                content: {
                    ...this.props.revision.content,
                },
            };
            optimisticResponse[mutationName].content[this.props.field] = this.state.edits;

            this.setState({ edits: null });

            applyMutation({
                variables,
                optimisticResponse,
            });
        }, 1000);
    }

    createMutation() {
        const {kind, field, type, fragment} = this.props;
        return gql`
            mutation Set${kind}${capitalize(field)}($id: String!, $${field}: ${type}!) {
                 set${kind}${capitalize(field)}(id: $id, ${field}: $${field}) {
                    ...${kind}Revision
                 }
            }
            ${fragment}`;
    }


    handleStringChange(event, applyMutation) {
        const val = event.target.value;
        this.setState({edits: val});
        this.sendMutation(applyMutation);
    }

    handleIntChange(event, applyMutation) {
        const val = +event.target.value;
        this.setState({edits: val});
        this.sendMutation(applyMutation);
    }

    handleBooleanChange(event, applyMutation) {
        const val = !!event.target.checked;
        this.setState({edits: val});
        this.sendMutation(applyMutation);
    }

    render() {
        return <p>
            {capitalize(this.props.field)}:{" "}
            <Mutation mutation={this.createMutation()}>
            {applyMutation => {
                switch (this.props.type) {
                    case "String":
                        return <input
                            onChange={event => this.handleStringChange(event, applyMutation)}
                            value={this.state.edits !== null ? this.state.edits : this.props.revision.content[this.props.field]}
                            />;
                    case "Int":
                        return <input
                            onChange={event => this.handleIntChange(event, applyMutation)}
                            type="number"
                            value={this.state.edits !== null ? this.state.edits : this.props.revision.content[this.props.field]}
                            />;
                    case "Boolean":
                        return <input
                            onChange={event => this.handleBooleanChange(event, applyMutation)}
                            type="checkbox"
                            checked={this.state.edits !== null ? this.state.edits : this.props.revision.content[this.props.field]}
                            />;
                }
                return <p>Unknown field type {this.props.type}</p>;
            }}
            </Mutation>
        </p>;
    }
}
