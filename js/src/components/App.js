import React from 'react';
import { hot } from 'react-hot-loader';
import { ApolloProvider } from "react-apollo";

import Puppies from "./Puppies.js";

class App extends React.Component {
  render() {
    return (
      <ApolloProvider client={this.props.client}>
        <div style={{ textAlign: 'center' }}>
          <h1>Hello, Kotlin and React!</h1>
          <Puppies />
        </div>
      </ApolloProvider>
    );
  }
}
  
export default hot(module)(App);