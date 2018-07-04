import React from 'react';
import { Query, Mutation } from "react-apollo";
import gql from "graphql-tag";
import FieldEditor from "./FieldEditor.js";

const videoFragment = `
fragment VideoRevision on VideoRevision {
    id
    sha
    content {
        duration
        slug
        title
        youtubeId
        hidden
    }
}
`;

const GQLVideoTitles = gql`
query GetAllVideos {
    getAllVideos {
        id
        content {
            title
        }
    }
}
`;

const GQLGetVideo = gql`
query GetVideo($id: String!) {
    getVideo(id: $id) {
        ...VideoRevision
    }
}
${videoFragment}
`;

const GQLCreateVideo = gql`
mutation CreateVideo($duration: Int!, $hidden: Boolean!, $slug: String!, $title: String!, $youtubeId: String!) {
    createVideo(duration: $duration, hidden: $hidden, slug: $slug, title: $title, youtubeId: $youtubeId) {
        ...VideoRevision
    }
}
${videoFragment}
`;

const NEW_VIDEO_ID = "NEW_VIDEO";

const NEW_VIDEO_PROPERTIES = {
    slug: "new-video",
    title: "New Video",
    duration: 0,
    hidden: false,
    youtubeId: "xyz123",
};

function updateVideoStore(proxy, response) {
    console.log("updateVideoStore", proxy, response);
    // TODO implement
    const qData = proxy.readQuery({ query: GQLVideoTitles });

    if (response.data.createVideo) {
        qData.getAllVideos.push(response.data.createVideo);
    }

    proxy.writeQuery({ query: GQLVideoTitles, data: qData });
}

export class VideoList extends React.Component {
    isSelected(kind, id) {
        return this.props.selected && this.props.selected.kind === kind &&
            this.props.selected.id === id;
    }

    render() {
        return <div>
            <h2>Videos</h2>
            <Query query={GQLVideoTitles}>
            {({ loading, error, data: qData }) => {
                if (loading) return <p>Loading...</p>;
                if (error) return <p>Error!</p>;

                return <ol>
                    {qData.getAllVideos.map(video => <li
                        key={video.id}
                        style={{ fontWeight: this.isSelected("Video", video.id) ? "bold" : "normal" }}
                        onClick={() => video.id !== NEW_VIDEO_ID && this.props.onSelect({kind: "Video", id: video.id})}
                    >
                        {video.content.title}
                        {video.id === NEW_VIDEO_ID && " (Saving...)"}
                    </li>)}
                    <li key="__NEW__">
                        <Mutation
                            mutation={GQLCreateVideo}
                            update={updateVideoStore}
                        >
                        {createVideo => <button
                            onClick={() => createVideo({
                                variables: NEW_VIDEO_PROPERTIES,
                                optimisticResponse: {
                                    createVideo: {
                                        __typename: "VideoRevision",
                                        id: NEW_VIDEO_ID,
                                        sha: "",
                                        content: {
                                            ...NEW_VIDEO_PROPERTIES,
                                            __typename: "Video",
                                        },
                                    },
                                },
                            })}
                        >
                            Create new video
                        </button>}
                        </Mutation>
                    </li>
                </ol>;
            }}
            </Query>
        </div>;
    }
}

export class VideoEditor extends React.Component {
    render() {
        return <Query query={GQLGetVideo} variables={{id: this.props.id}}>
            {({ loading, error, data: qData }) => {
                if (loading) return <p>Loading...</p>;
                if (error) return <p>Error!</p>;
                const videoRevision = qData.getVideo;
                return <div>
                    <h3>Video {this.props.id}</h3>
                    <p>SHA: {videoRevision.sha}</p>
                    <FieldEditor
                        field="slug"
                        fragment={videoFragment}
                        kind="Video"
                        type="String"
                        revision={videoRevision}
                    />
                    <FieldEditor
                        field="title"
                        fragment={videoFragment}
                        kind="Video"
                        type="String"
                        revision={videoRevision}
                    />
                    <FieldEditor
                        field="youtubeId"
                        fragment={videoFragment}
                        kind="Video"
                        type="String"
                        revision={videoRevision}
                    />
                    <FieldEditor
                        field="duration"
                        fragment={videoFragment}
                        kind="Video"
                        type="Int"
                        revision={videoRevision}
                    />
                    <FieldEditor
                        field="hidden"
                        fragment={videoFragment}
                        kind="Video"
                        type="Boolean"
                        revision={videoRevision}
                    />
                </div>;
            }}
        </Query>;
    }
}
