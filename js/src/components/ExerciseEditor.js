import React from 'react';
import { Query, Mutation } from "react-apollo";
import gql from "graphql-tag";
import FieldEditor from "./FieldEditor.js";

const exerciseFragment = `
fragment ExerciseRevision on ExerciseRevision {
    id
    sha
    content {
        slug
        title
    }
}
`;

const GQLExerciseTitles = gql`
query GetAllExercises {
    getAllExercises {
        id
        content {
            title
        }
    }
}
`;

const GQLGetExercise = gql`
query GetExercise($id: String!) {
    getExercise(id: $id) {
        ...ExerciseRevision
    }
}
${exerciseFragment}
`;

const GQLCreateExercise = gql`
mutation CreateExercise($slug: String!, $title: String!) {
    createExercise(slug: $slug, title: $title) {
        ...ExerciseRevision
    }
}
${exerciseFragment}
`;

const NEW_EXERCISE_ID = "NEW_EXERCISE";

const NEW_EXERCISE_PROPERTIES = {
    slug: "new-exercise",
    title: "New Exercise",
};

function updateExerciseStore(proxy, response) {
    console.log("updateExerciseStore", proxy, response);
    // TODO implement
    const qData = proxy.readQuery({ query: GQLExerciseTitles });

    if (response.data.createExercise) {
        qData.getAllExercises.push(response.data.createExercise);
    }

    proxy.writeQuery({ query: GQLExerciseTitles, data: qData });
}

export class ExerciseList extends React.Component {
    isSelected(kind, id) {
        return this.props.selected && this.props.selected.kind === kind &&
            this.props.selected.id === id;
    }

    render() {
        return <div>
            <h2>Exercises</h2>
            <Query query={GQLExerciseTitles}>
            {({ loading, error, data: qData }) => {
                if (loading) return <p>Loading...</p>;
                if (error) return <p>Error!</p>;

                return <ol>
                    {qData.getAllExercises.map(exercise => <li
                        key={exercise.id}
                        style={{ fontWeight: this.isSelected("Exercise", exercise.id) ? "bold" : "normal" }}
                        onClick={() => exercise.id !== NEW_EXERCISE_ID && this.props.onSelect({kind: "Exercise", id: exercise.id})}
                    >
                        {exercise.content.title}
                        {exercise.id === NEW_EXERCISE_ID && " (Saving...)"}
                    </li>)}
                    <li key="__NEW__">
                        <Mutation
                            mutation={GQLCreateExercise}
                            update={updateExerciseStore}
                        >
                        {createExercise => <button
                            onClick={() => createExercise({
                                variables: NEW_EXERCISE_PROPERTIES,
                                optimisticResponse: {
                                    createExercise: {
                                        __typename: "ExerciseRevision",
                                        id: NEW_EXERCISE_ID,
                                        sha: "",
                                        content: {
                                            ...NEW_EXERCISE_PROPERTIES,
                                            __typename: "Exercise",
                                        },
                                    },
                                },
                            })}
                        >
                            Create new exercise
                        </button>}
                        </Mutation>
                    </li>
                </ol>;
            }}
            </Query>
        </div>;
    }
}

export class ExerciseEditor extends React.Component {
    render() {
        return <Query query={GQLGetExercise} variables={{id: this.props.id}}>
            {({ loading, error, data: qData }) => {
                if (loading) return <p>Loading...</p>;
                if (error) return <p>Error!</p>;
                const exerciseRevision = qData.getExercise;
                return <div>
                    <h3>Exercise {this.props.id}</h3>
                    <p>SHA: {exerciseRevision.sha}</p>
                    <FieldEditor
                        field="slug"
                        fragment={exerciseFragment}
                        kind="Exercise"
                        type="String"
                        revision={exerciseRevision}
                    />
                    <FieldEditor
                        field="title"
                        fragment={exerciseFragment}
                        kind="Exercise"
                        type="String"
                        revision={exerciseRevision}
                    />
                </div>;
            }}
        </Query>;
    }
}
