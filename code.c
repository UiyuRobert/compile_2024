void main() {
	Z();
}

void Z() {
	sys = getnext();
	if (sys == 'c') {
		A(sys);
		sys = getnext();
		if (sys == 'c') {
			sys = getnext();
			B(sys);
			sys = getnext();
			if (sys == '$') success;
			else error;
		}
		else error;
	}
	else if (sys == 'a') {
		B(sys);
		sys = getnext();
		if (sys == 'd') {
			success;
		}
		else error;
	}
	else error;
}

void A(sys) {
	if (sys == c) {
		sys = getnext();
		A'(sys);
	}
	else error;
}

void B(sys) {
	if (sys == a) {
		sys = getnext();
		if (sys == a) B(sys);
		else {
			reverse();
			return;
		}
	}
	else {
		reverse();
		return;
	}
}
void A'(sys) {
	if (sys == 'a') {
		sys = getnext();
		B(sys);
		sys = getnext();
		A'(sys);
	}
	else {
		reverse();
		return;
	}
}
