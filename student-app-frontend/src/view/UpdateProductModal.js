import * as React from 'react';

import Button from '@material-ui/core/Button';
import TextField from '@material-ui/core/TextField';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import Alert from '@material-ui/lab/Alert';
import CircularProgress from '@material-ui/core/CircularProgress';

import {useState} from "react";
import {makeStyles} from "@material-ui/core/styles";

const useStyles = makeStyles({
	dialogPaper: {
		minHeight: '50%',
		minWidth: '50%',
	},
	alertError: {
		marginBottom: '1.5rem'
	}
});


export default function UpdateProductModal(props) {

	const classes = useStyles();

	const [error, setError] = useState(false)

	const [state, setState] = useState({loading: false});

	const [data, updateData] = useState({name: props.data.name, description: props.data.description, price: props.data.price});

	const [serverError, setServerError] = useState("")

	const handleChange = (e) => {
		updateData({
			...data,
			// Trimming any whitespace
			[e.target.name]: e.target.value.trim()
		});
	};

	async function handleSubmit(e) {
		e.preventDefault()

		// Check the data is valid or exists
		const valid = Object.values(data).every(x => (x !== null && x !== ''));
		setError(!valid)
		if(!valid) {
			return;
		}

		setState({ ...state, loading: true});
		const response = await fetch(`/products/${props.data.id}`, {
			method: 'PUT',
			headers: {
				'Accept': 'application/json',
				'Content-Type': 'application/json'
			},
			body: JSON.stringify(data)
		});

		if(response.status === 200) {
			props.handleClose()
			setServerError("")
		} else {
			// Show error
			const content = await response.json();
			console.log(content)
			setServerError(content)
		}

		setState({ ...state, loading: false})
	}

	return (
		<form action={`/products/${props.data.id}`} method={"PUT"} >
			<Dialog classes={{ paper: classes.dialogPaper }} open={props.open} onClose={props.handleClose} aria-labelledby="form-dialog-title">
				<DialogTitle id="form-dialog-title">Update Product</DialogTitle>
				<DialogContent>
					<DialogContentText>
						Here you can edit your product!
					</DialogContentText>
					<div>
						{serverError ? <Alert className={classes.alertError} severity="error">
								{serverError.title}
								{Array.from(serverError.violations).map( v => {
									return <p key={v}><strong>- {v.field.split(".")[2]} </strong>{v.message}</p>
								})}
							</Alert>
							: ""}
						<div >
							<TextField
								error={error}
								variant={"outlined"}
								autoFocus
								margin="dense"
								name="name"
								label="Product name"
								type="text"
								defaultValue={props.data.name}
								helperText={error ? "The product must have a name." : ""}
								required={true}
								fullWidth={true}
								onChange={handleChange}
							/>
						</div>
						<div>
							<TextField
								error={error}
								variant={"outlined"}
								multiline
								rows={4}
								autoFocus
								margin="dense"
								name="description"
								label="Description"
								type="text"
								defaultValue={props.data.description}
								helperText={error ? "It is required a description about the product." : ""}
								required={true}
								fullWidth={true}
								onChange={handleChange}
							/>
						</div>
						<div>
							<TextField
								error={error}
								variant={"outlined"}
								autoFocus
								margin="dense"
								name="price"
								label="Price"
								type="number"
								defaultValue={props.data.price}
								helperText={error ? "The price should be greater than 0." : ""}
								required={true}
								fullWidth={true}
								onChange={handleChange}
							/>
						</div>
					</div>



				</DialogContent>
				<DialogActions>
					{state.loading ? <CircularProgress /> : ''}
					<Button variant={"outlined"} onClick={props.handleClose} color="primary">
						Cancel
					</Button>
					<Button type="submit" onClick={handleSubmit} variant={"outlined"} color="primary">
						Save
					</Button>
				</DialogActions>
			</Dialog>
		</form>
	);
}
