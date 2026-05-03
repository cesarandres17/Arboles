package bst;

import java.util.*;

public class ArbolBinario {

    Nodo raiz;

    public ArbolBinario() {
        this.raiz = null;
    }

    public boolean estaVacio() {
        return raiz == null;
    }

    public boolean agregar(int dato) {
        if (existe(dato))
            return false;
        raiz = agregarRec(raiz, dato);
        return true;
    }

    private Nodo agregarRec(Nodo nodo, int dato) {
        if (nodo == null)
            return new Nodo(dato);
        if (dato < nodo.dato)
            nodo.izq = agregarRec(nodo.izq, dato);
        else if (dato > nodo.dato)
            nodo.der = agregarRec(nodo.der, dato);
        return nodo;
    }

    public boolean eliminar(int dato) {
        if (!existe(dato))
            return false;
        raiz = eliminarRec(raiz, dato);
        return true;
    }

    private Nodo eliminarRec(Nodo nodo, int dato) {
        if (nodo == null)
            return null;
        if (dato < nodo.dato) {
            nodo.izq = eliminarRec(nodo.izq, dato);
        } else if (dato > nodo.dato) {
            nodo.der = eliminarRec(nodo.der, dato);
        } else {
            if (nodo.izq == null)
                return nodo.der;
            if (nodo.der == null)
                return nodo.izq;
            int sucesor = obtenerMenorRec(nodo.der);
            nodo.dato = sucesor;
            nodo.der = eliminarRec(nodo.der, sucesor);
        }
        return nodo;
    }

    public boolean existe(int dato) {
        return existeRec(raiz, dato);
    }

    private boolean existeRec(Nodo nodo, int dato) {
        if (nodo == null)
            return false;
        if (dato == nodo.dato)
            return true;
        if (dato < nodo.dato)
            return existeRec(nodo.izq, dato);
        return existeRec(nodo.der, dato);
    }

    public void inorden(Nodo nodo, List<Integer> lista) {
        if (nodo == null)
            return;
        inorden(nodo.izq, lista);
        lista.add(nodo.dato);
        inorden(nodo.der, lista);
    }

    public void preorden(Nodo nodo, List<Integer> lista) {
        if (nodo == null)
            return;
        lista.add(nodo.dato);
        preorden(nodo.izq, lista);
        preorden(nodo.der, lista);
    }

    public void postorden(Nodo nodo, List<Integer> lista) {
        if (nodo == null)
            return;
        postorden(nodo.izq, lista);
        postorden(nodo.der, lista);
        lista.add(nodo.dato);
    }

    public int obtenerPeso() {
        return obtenerPesoRec(raiz);
    }

    private int obtenerPesoRec(Nodo nodo) {
        if (nodo == null)
            return 0;
        return 1 + obtenerPesoRec(nodo.izq) + obtenerPesoRec(nodo.der);
    }

    public int obtenerAltura() {
        return obtenerAlturaRec(raiz);
    }

    private int obtenerAlturaRec(Nodo nodo) {
        if (nodo == null)
            return 0;
        int altIzq = obtenerAlturaRec(nodo.izq);
        int altDer = obtenerAlturaRec(nodo.der);
        return 1 + Math.max(altIzq, altDer);
    }

    public int obtenerNivel(int dato) {
        return obtenerNivelRec(raiz, dato, 0);
    }

    private int obtenerNivelRec(Nodo nodo, int dato, int nivel) {
        if (nodo == null)
            return -1;
        if (nodo.dato == dato)
            return nivel;
        if (dato < nodo.dato)
            return obtenerNivelRec(nodo.izq, dato, nivel + 1);
        return obtenerNivelRec(nodo.der, dato, nivel + 1);
    }

    public int contarHojas() {
        return contarHojasRec(raiz);
    }

    private int contarHojasRec(Nodo nodo) {
        if (nodo == null)
            return 0;
        if (nodo.izq == null && nodo.der == null)
            return 1;
        return contarHojasRec(nodo.izq) + contarHojasRec(nodo.der);
    }

    public int obtenerMenor() {
        return obtenerMenorRec(raiz);
    }

    private int obtenerMenorRec(Nodo nodo) {
        if (nodo.izq == null)
            return nodo.dato;
        return obtenerMenorRec(nodo.izq);
    }

    public int obtenerMayor() {
        return obtenerMayorRec(raiz);
    }

    private int obtenerMayorRec(Nodo nodo) {
        if (nodo.der == null)
            return nodo.dato;
        return obtenerMayorRec(nodo.der);
    }

    public List<Integer> imprimirAmplitud() {
        List<Integer> lista = new ArrayList<>();
        if (raiz == null)
            return lista;
        Queue<Nodo> cola = new LinkedList<>();
        cola.add(raiz);
        while (!cola.isEmpty()) {
            Nodo actual = cola.poll();
            lista.add(actual.dato);
            if (actual.izq != null)
                cola.add(actual.izq);
            if (actual.der != null)
                cola.add(actual.der);
        }
        return lista;
    }

    public void borrarArbol() {
        raiz = null;
    }

    public String toJson() {
        if (raiz == null)
            return "null";
        return nodoToJson(raiz);
    }

    private String nodoToJson(Nodo nodo) {
        if (nodo == null)
            return "null";
        return "{\"dato\":" + nodo.dato +
                ",\"izq\":" + nodoToJson(nodo.izq) +
                ",\"der\":" + nodoToJson(nodo.der) + "}";
    }
}
