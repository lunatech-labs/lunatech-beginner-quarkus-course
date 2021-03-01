import React from "react";
import LoadingCircular from "./view/LoadingCircular";
import {Container, Grid, withStyles} from "@material-ui/core";
import ProductCard from "./view/ProductCard";

const styles=  (theme) => ({
    catalogContainer: {
        flexGrow: 1,
        marginLeft: '3rem',
        marginRight: '3rem'


    }
})

class Catalogue extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            error: null,
            isLoaded: false,
            products: []
        };
        if(props.featureFlags.reactivePrices) {
            this.eventSource = new EventSource("http://localhost:8080/prices/stream");
        }
    }

    componentDidMount() {
        fetch("http://localhost:8080/products")
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

        if(this.eventSource !== undefined) {
            this.eventSource.onmessage = e => {
                this.updatePrice(JSON.parse(e.data));
            }
        }
    }

    updatePrice(data) {
        this.setState(prevState => ({
            products: prevState.products.map((product) => {
                if (product.id === data.productId) {
                    product.price = data.price;
                }
                return product;
            })
        }));
    }

    render() {
        const { error, isLoaded, products } = this.state;

        const { classes } = this.props;

        if (error) {
            return <div>Error: {error.message}</div>;
        } else if (!isLoaded) {
            return <LoadingCircular />;
        } else {
            let productLine;
            if (this.props.featureFlags.productDetails) {
                productLine = (product) => (<span><a href={'/products/' + product.id}>{product.name}</a> - € {product.price}</span>);
            } else {
                productLine = (product) => (<span>{product.name} - € {product.price}</span>);
            }

            return (
                <Container className={classes.catalogContainer}>
                    <h2>Catalogue</h2>

                    <Grid container spacing={3} >

                        {products.map(product => (
                            <Grid item xs={4} key={product.id} >
                                <ProductCard data={product}/>
                            </Grid>
                        ))}
                    </Grid>
                </Container>
            );
        }
    }

}

export default withStyles(styles) (Catalogue)
