import React from "react";
import LoadingCircular from "./view/LoadingCircular";
import {Container, Grid, Typography, withStyles} from "@material-ui/core";
import ProductCard from "./view/ProductCard";

const styles=  (theme) => ({
    catalogContainer: {
        flexGrow: 1,
        marginLeft: '4rem',
        marginRight: '4rem',
        marginTop: "2rem"

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
            this.eventSource = new EventSource("/prices/stream");
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

    componentDidMount() {

        fetch("/products")
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

    componentWillUnmount() {
        if(this.props.featureFlags.reactivePrices) {
            this.eventSource.close();
        }
    }


    render() {
        const { error, isLoaded, products } = this.state;

        const { classes } = this.props;

        if (error) {
            return <div>Error: {error.message}</div>;
        } else if (!isLoaded) {
            return <LoadingCircular />;
        } else {
            return (
                <Container className={classes.catalogContainer}>

                    <Typography gutterBottom variant="h3" color={"primary"} >
                        Catalogue
                    </Typography>

                    <Grid container spacing={4} >

                        { products.map(product => (
                            <Grid item xs={6} sm={4} md={3} lg={3} key={product.id} >
                                <ProductCard data={product} enabled={this.props.featureFlags.productDetails}/>
                            </Grid>
                        ))}
                    </Grid>
                </Container>
            );
        }
    }

}

export default withStyles(styles) (Catalogue)
