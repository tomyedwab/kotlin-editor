import React from 'react';
import ReactDOM from 'react-dom';
import App from './components/App.js';
import ApolloClient from "apollo-boost";
import gql from "graphql-tag";

const client = new ApolloClient({
    uri: "/graphql"
});

window.client = client;
window.gql = gql;

ReactDOM.render(<App client={client} />, document.getElementById('root'));