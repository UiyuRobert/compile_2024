char s[10];
int dd[10] = {4,5};

int add(int x, int y, int tt[]) {
    x = x + x;
    return x + y + tt[3];
}

int main() {
    const int b = 2;
    const int c_[4] = {3,5,6,0};
    s[2] = 9;
    int c;
    c = dd[0];
    c = add(b, dd[1], c_);
    return c;
}
