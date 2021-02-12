import './App.css';
import React from "react";
import {
  BrowserRouter as Router,
  Switch,
  Route,
} from "react-router-dom";
import SearchResult from "./SearchResult";
import Catalogue from "./Catalogue";
import ProductDetails from "./ProductDetails";
import Navbar from "./Navbar";

function App() {
  return (
      <Router>
        <div class="container">
          <Navbar />
          <Switch>
            <Route path="/search/:query" component={SearchResult} />
            <Route path="/products/:id" component={ProductDetails} />
            <Route component={Catalogue} />
          </Switch>
          </div>
      </Router>
  );
}

export default App;
