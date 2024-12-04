// Global declarations
#include<stdio.h>
const int constIntArray[3] = {10, 20, 30};       // Constant integer array
const char constCharArray[5] = {'A', 'B', 'C', 'D', 'E'}; // Constant character array
const char constCharArray2[5] = "abc"; // Constant character array
int intArray[5];                                 // Integer array
char charArray[5];                               // Character array

int main() {

    // Calculate sum of ASCII codes
    int asciiSum = constCharArray2[0] + constCharArray2[1] + constCharArray2[2] + constCharArray2[3] + constCharArray2[4];
    char charSum = constCharArray2[0] + constCharArray2[1] + constCharArray2[2] + constCharArray2[3] + constCharArray2[4];

    printf("Sum of ASCII codes2: %d %c\n", asciiSum, charSum);

    return 0;
}
