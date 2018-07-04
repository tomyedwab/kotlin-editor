import React from 'react';
import { hot } from 'react-hot-loader';
import { ApolloProvider } from "react-apollo";
import ContentList from "./ContentList.js"
import EditPane from "./EditPane.js"

class App extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            selected: null,
        };
    }

    render() {
        return (
          <ApolloProvider client={this.props.client}>
            <div style={{ display: "flex", flexDirection: "column" }}>
                <div style={{ textAlign: 'center' }}>
                  <h1>Example editor</h1>
                </div>
                <div style={{ display: "flex", flexDirection: "row" }}>
                  <ContentList
                    selected={this.state.selected}
                    onSelect={item => this.setState({selected: item})}
                  />
                  <EditPane
                    selected={this.state.selected}
                  />
                </div>
             </div>
          </ApolloProvider>
        );
    }
}
  
export default hot(module)(App);