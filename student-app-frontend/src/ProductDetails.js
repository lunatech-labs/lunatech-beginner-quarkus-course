import React from "react";

class ProductDetails extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            error: null,
            isLoaded: false,
            product: null
        };

        if(props.featureFlags.reactivePrices) {
            this.eventSource = new EventSource("http://localhost:8080/prices/stream/" + props.match.params.id);
        }

    }

    componentDidMount() {
        const { match: { params } } = this.props;

        fetch("http://localhost:8080/products/" + params.id)
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
        this.eventSource.close()
    }

    render() {
        const { error, isLoaded, product } = this.state;
        if (error) {
            return <div>Error: {error.message}</div>;
        } else if (!isLoaded) {
            return <div>Loading...</div>;
        } else {
            return (
                <div>
                  <h1>{product.name}</h1>
                  <dl>
                      <dt>Price</dt>
                      <dd>€ {product.price}</dd>
                      <dt>Description</dt>
                      <dd>{product.description}</dd>
                  </dl>
                </div>
            );
        }
    }
}

export default ProductDetails
