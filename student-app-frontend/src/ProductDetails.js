import React from "react";
import {Container, withStyles} from "@material-ui/core";
import Typography from "@material-ui/core/Typography";
import CardMedia from "@material-ui/core/CardMedia";

const styles =  (theme) => ({
    productContainer: {
        flexGrow: 1,
        marginLeft: '4rem',
        marginRight: '4rem',
        marginTop: "2rem"

    }
})

class ProductDetails extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            error: null,
            isLoaded: false,
            product: null
        };

        if(props.featureFlags.reactivePrices) {
            this.eventSource = new EventSource("/prices/stream/" + props.match.params.id);
        }

    }

    componentDidMount() {
        const { match: { params } } = this.props;

        fetch("/products/" + params.id)
            .then(res => res.json())
            .then(
                (result) => {
                    this.setState({
                        isLoaded: true,
                        product: result
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
                this.setState(prevState => {
                    let updatedProduct = prevState.product;
                    updatedProduct.price = JSON.parse(e.data).price;
                    return {
                        product: updatedProduct
                    }
                });
            }
        }
    }

    componentWillUnmount() {
        if(this.props.featureFlags.reactivePrices) {
            this.eventSource.close()
        }
    }

    render() {

        const { classes } = this.props;

        const { error, isLoaded, product } = this.state;
        if (error) {
            return <div>Error: {error.message}</div>;
        } else if (!isLoaded) {
            return <div>Loading...</div>;
        } else {
            return (
                <Container className={classes.productContainer}>
                    <Typography color={"primary"} gutterBottom variant="h3" component="h2">
                        {product.name}
                    </Typography>
                    <img alt={product.name} src={`https://fakeimg.pl/420x320/ff0000,128/333333,255/?text=${product.name}&font=lobster`} />
                  <dl>
                      <Typography color={"secondary"} gutterBottom variant="h6" component="h4">Price</Typography>
                      <Typography inline={"true"} align={"left"} color="primary" component="div">${product.price}</Typography>
                      <Typography color={"secondary"} gutterBottom variant="h6" component="h4">Description</Typography>
                      <Typography inline={"true"} align={"left"} color="primary" component="div">${product.description}</Typography>
                  </dl>
                </Container>
            );
        }
    }
}

export default withStyles(styles) (ProductDetails)
