
import React from "react";
import LoadingCircular from "./LoadingCircular";
import {Container, Grid, Typography, withStyles} from "@material-ui/core";
import ProductCard from "./ProductCard";

const styles=  (theme) => ({
    catalogContainer: {
        flexGrow: 1,
        marginLeft: '4rem',
        marginRight: '4rem',
        marginTop: "2rem"

    }
})

class SearchResult extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            error: null,
            isLoaded: false,
            products: []
        };
    }

    componentDidMount() {
        const {match: {params: {query}}} = this.props;

        fetch("/products/search/" + query)
            .then(res => res.json())
            .then(
                (result) => {
                    this.setState({
                        isLoaded: true,
                        products: result
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

        const { classes } = this.props;

        const { error, isLoaded, products } = this.state;
        if (error) {
            return <div>Error: {error.message}</div>;
        } else if (!isLoaded) {
            return <LoadingCircular/>;
        } else {
            return (
                <Container className={classes.searchContainer}>

                    <Typography gutterBottom variant="h3" color={"primary"} >
                        Search
                    </Typography>

                    <Grid container spacing={4} >

                        { products.map(product => (
                            <Grid item xs={6} sm={4} md={3} lg={3} key={product.id} >
                                <ProductCard data={product} editEnabled={this.props.featureFlags.productUpdate} enabled={this.props.featureFlags.productDetails}/>
                            </Grid>
                        ))}
                    </Grid>
                </Container>
            );
        }
    }

}

export default withStyles(styles) (SearchResult)
