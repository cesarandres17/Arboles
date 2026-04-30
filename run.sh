#!/bin/bash
echo "Compilando..."
mkdir -p out
javac -d out src/bst/*.java
if [ $? -eq 0 ]; then
    echo "Compilación exitosa. Iniciando servidor..."
    java -cp out bst.BSTServer
else
    echo "Error de compilación"
fi