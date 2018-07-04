import React from 'react';
import { Query, Mutation } from "react-apollo";
import gql from "graphql-tag";

import { ExerciseList } from "./ExerciseEditor.js";
import { VideoList } from "./VideoEditor.js";

export default class ContentList extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        return <div style={{ flex: "1 0 0" }}>
            <VideoList selected={this.props.selected} onSelect={this.props.onSelect} />
            <ExerciseList selected={this.props.selected} onSelect={this.props.onSelect} />
         </div>;
    }
}
