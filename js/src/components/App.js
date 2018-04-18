import React from 'react';
import { hot } from 'react-hot-loader';

class App extends React.Component {
  render() {
    return (
     <div style={{textAlign: 'center'}}>
        <h1>Hello, Kotlin and React!</h1>
      </div>);
  }
}

const HotApp = () => <App />;
export default hot(module)(HotApp);