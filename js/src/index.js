import React from 'react';
import ReactDOM from 'react-dom';
import App from './components/App.js';
import ApolloClient from "apollo-boost";
import { InMemoryCache } from 'apollo-cache-inmemory';
import gql from "graphql-tag";

const cache = new InMemoryCache();

const client = new ApolloClient({
    uri: "/graphql",
    cache
});

window.client = client;
window.gql = gql;

ReactDOM.render(<App client={client} />, document.getElementById('root'));