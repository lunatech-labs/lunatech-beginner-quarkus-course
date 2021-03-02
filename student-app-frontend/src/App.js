import './App.css';
import React from "react";
import {
    Switch,
    Route, BrowserRouter, HashRouter,
} from "react-router-dom";

import SearchResult from "./SearchResult";
import Catalogue from "./Catalogue";
import ProductDetails from "./ProductDetails";
import Navbar from "./view/Navbar";

import LoadingCircular from "./view/LoadingCircular";
import {Router} from "@material-ui/icons";

class App extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      isLoaded:false,
      featureFlags: null
    }
  }

  componentDidMount() {
    fetch("http://localhost:8080/feature-flags")
        .then(res => res.json())
        .then(
            (result) => {
              this.setState({
                isLoaded: true,
                featureFlags: result
              });
            },
            // Note: it's important to handle errors here
            // instead of a catch() block so that we don't swallow
            // exceptions from actual bugs in components.
            (error) => {
              this.setState({
                isLoaded: true,
                error
              });
            }
        )
  }

    render() {
      if(this.state.isLoaded) {
          const featureFlags = this.state.featureFlags;
          return (
              <HashRouter basename={"/"}>
                  <Navbar featureFlags={featureFlags} />
                  <Switch>
                      <Route
                          path="/search/:query"
                          render={(props) =>
                              (<SearchResult {...props} featureFlags={featureFlags} />)} />

                      <Route
                          path="/products/:id"
                          render={(props) =>
                              (<ProductDetails {...props} featureFlags={featureFlags} />)} />

                      <Route
                          render={props =>
                              (<Catalogue {...props} featureFlags={featureFlags} />)} />
                  </Switch>
              </HashRouter>
          )
      } else {
          return <LoadingCircular />;
      }
    }
}

export default App;
