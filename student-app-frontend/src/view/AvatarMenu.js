import * as React from 'react';
import {createRef, Fragment, useRef} from "react";
import {Avatar, Button, Menu, MenuItem} from "@material-ui/core";
import {AccountCircle} from "@material-ui/icons";
import {makeStyles} from "@material-ui/core/styles";

const useStyles = makeStyles((theme) => ({
	avatar: {
		marginLeft: '2rem',
	},
	avatarIcon: {
		width: theme.spacing(6),
		height: theme.spacing(6),
	}
})
)

export function AvatarMenu() {

	const classes = useStyles()

	const ref = createRef()

	const [anchorEl, setAnchorEl] = React.useState(null);

	const handleClick = (event) => {
		setAnchorEl(event.currentTarget);
	};

	const handleClose = () => {
		setAnchorEl(null);
	};

	return (
		<Fragment>
			<Button aria-controls="simple-menu" aria-haspopup="true" onClick={handleClick}>
				<Avatar className={classes.avatar}>
					<AccountCircle className={classes.avatarIcon}/>
				</Avatar>

			</Button>
			<Menu ref={ref}
			      id="simple-menu"
				anchorEl={anchorEl}
				keepMounted
				open={Boolean(anchorEl)}
				onClose={handleClose}
			>
				<MenuItem onClick={handleClose}>Profile</MenuItem>
				<MenuItem onClick={handleClose}>My account</MenuItem>
				<MenuItem onClick={handleClose}>Logout</MenuItem>
			</Menu>
		</Fragment>
	);
}