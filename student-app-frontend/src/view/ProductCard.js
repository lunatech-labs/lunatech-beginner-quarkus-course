import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import Card from '@material-ui/core/Card';
import CardActionArea from '@material-ui/core/CardActionArea';
import CardActions from '@material-ui/core/CardActions';
import CardContent from '@material-ui/core/CardContent';
import CardMedia from '@material-ui/core/CardMedia';
import Button from '@material-ui/core/Button';
import Typography from '@material-ui/core/Typography';

const useStyles = makeStyles({
	root: {
		maxWidth: 345,
	},
	price: {
		fontWeight: 900,
		fontSize: '1.5rem'
	}
});

export default function ProductCard (props) {

	const classes = useStyles();

	return (
		<Card className={classes.root}>
			<CardActionArea>
				<CardMedia
					component="img"
					alt="Contemplative Reptile"
					height="140"
					image={`https://fakeimg.pl/420x320/ff0000,128/333333,255/?text=${props.data.name}&font=lobster`}
					title="Contemplative Reptile"
				/>
				<CardContent>
					<Typography gutterBottom variant="h5" component="h2">
						{props.data.name}
					</Typography>
					<Typography variant="body2" color="textSecondary" component="p">
						{props.data.description}
					</Typography>
					<Typography inline={"true"} align={"right"} color="primary" className={classes.price} component="div">
						${props.data.price}
					</Typography>

				</CardContent>
			</CardActionArea>
			<CardActions>
				<Button size="small" color="primary">
					Edit
				</Button>
				<Button size="small" color="primary">
					Learn More
				</Button>
			</CardActions>
		</Card>
	);
}
