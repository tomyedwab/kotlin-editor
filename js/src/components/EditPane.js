import React from 'react';

import { ExerciseEditor } from "./ExerciseEditor.js";
import { VideoEditor } from "./VideoEditor.js";

export default class ContentList extends React.Component {
    render() {
        let contents = <p>No selection</p>;
        if (this.props.selected) {
            switch (this.props.selected.kind) {
                case "Video":
                    contents = <VideoEditor id={this.props.selected.id} />;
                    break
                case "Exercise":
                    contents = <ExerciseEditor id={this.props.selected.id} />;
                    break
            }
        }

        return <div style={{ flex: "1 0 0" }}>
            {contents}
        </div>;
    }
}
