int a[4] = {4,5};
int main() {
    if (a[0] > 3 || a[2] != 0)
        a[0] = 0;
    else
        a[0] = 1;
    a[1] = 9;
    return 0;
}