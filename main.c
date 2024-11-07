int a[3 + 3] = {1, 2, 3, 4, 5, 6};
int foo(int x, int y[]) {
    return x + y[2];
}

int fee(int x, int y[]) {
    return foo(x, y);
}

int main() {
    int x = fee(a[4], a);

    return 0;
}