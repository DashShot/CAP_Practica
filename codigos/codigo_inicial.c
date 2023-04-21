
/*
La función printNumbersAsString convierte la matriz de números enteros que recibe como argumento y en la que se
encuentra codificado un texto en cadenas de caracteres de C que imprime por consola. Esta función permite pasar
de los valores enteros con los que trabaja el mecanismo de cifrado y descifrado a caracteres de texto imprimibles por
pantalla. Se puede emplear tanto para el texto cifrado como para el descifrado finalmente.
*/
void printNumbersAsString(int lines[nLines][nCharsPerLine])
{
    for (int idx = 0; idx < nLines; idx++)
    {
        char line[nCharsPerLine + 1];
        for (int idx2 = 0; idx2 < nCharsPerLine; idx2++)
        {
            line[idx2] = lines[idx][idx2];
        }
        line[nCharsPerLine] = '\0';
        printf("%s\n", line);
    }
}
/*
 La función decipher realiza el proceso de descifrado conforme al algoritmo de enigma. Para ello somete al proceso
de descifrado al array de datos enteros recibido como argumento conforme a la clave recibida igualmente como
argumento. El número de cifras de la clave determina automáticamente el número de rotores o etapas de cifrado del
mecanismo
*/
int *decipher(int line[], int key)
{
    int rawData[nCharsPerLine];
    for (int idx = 0; idx < nCharsPerLine; idx++)
    {
        rawData[idx] = line[idx];
    }
    int *rotorKeys = (int *)malloc(sizeof(int) * nRotors);

    int remainder = key;
    for (int idx = 0; idx < nRotors; idx++)
    {
        int divisor = pow(10, (nRotors - (1 + idx)));
        rotorKeys[idx] = (int)(remainder / divisor);
        remainder = (int)(remainder % divisor);
    }
    for (int rotorIdx = 0; rotorIdx < nRotors; rotorIdx++)
    {
        int displacement = rotorKeys[rotorIdx];
        for (int idx = 0; idx < nCharsPerLine; idx++)
        {
            rawData[idx] = rawData[idx] - displacement++;
        }
    }
    free(rotorKeys);
    return rawData;
}
/*La función enigma contiene el algoritmo general del programa que se encarga de, por cada línea del texto de entrada,
probar una decodificación con cada una de las claves posibles y comprobar si el resultado es el correcto o no (para
ello compara los primeros caracteres de la línea decodificada con la clave con la que se ha intentado descifrar, en
caso de que coincida el descifrado se ha realizado con éxito y se agrega esta línea al texto de salida final).*/
void enigma()
{
    printf("ESTO ES LA ENTRADA: \n");
    printNumbersAsString(ciphered_keys2);
    printf("\n");
    printf("\n");
    int deciphered[nLines][nCharsPerLine];
    for (int idx = 0; idx < nLines; idx++)
    {
        for (int lineKey = (int)pow(10, nRotors - 1); lineKey < (int)pow(10,
                                                                         nRotors);
             lineKey++)
        {
            int *p_deciphered = decipher(ciphered_keys2[idx], lineKey);
            char decipheredLine[nCharsPerLine];
            for (int idx = 0; idx < nCharsPerLine; idx++)
            {
                decipheredLine[idx] = p_deciphered[idx];
            }
            char stringKey[nRotors + 1];
            sprintf_s(stringKey, "%d", lineKey);
            if (!strncmp(stringKey, decipheredLine, nRotors))
            {
                for (int idx2 = 0; idx2 < nCharsPerLine; idx2++)
                {
                    deciphered[idx][idx2] = decipheredLine[idx2];
                }
                printf("Descifrada linea para %d\n", lineKey);
                break;
            }
        }
    }
    printf("\n");
    printf("ESTO ES LA SALIDA:\n");
    printNumbersAsString(deciphered);
    printf("\n");
    printf("\n");
}
