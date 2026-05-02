package bst;

import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class BSTServer {

    static ArbolBinario arbol = new ArbolBinario();

    public static void main(String[] args) throws Exception {
        int port = 8060;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/api/estaVacio", BSTServer::handleEstaVacio);
        server.createContext("/api/agregar", BSTServer::handleAgregar);
        server.createContext("/api/eliminar", BSTServer::handleEliminar);
        server.createContext("/api/existe", BSTServer::handleExiste);
        server.createContext("/api/recorrer", BSTServer::handleRecorrer);
        server.createContext("/api/peso", BSTServer::handlePeso);
        server.createContext("/api/altura", BSTServer::handleAltura);
        server.createContext("/api/nivel", BSTServer::handleNivel);
        server.createContext("/api/hojas", BSTServer::handleHojas);
        server.createContext("/api/menor", BSTServer::handleMenor);
        server.createContext("/api/mayor", BSTServer::handleMayor);
        server.createContext("/api/amplitud", BSTServer::handleAmplitud);
        server.createContext("/api/borrar", BSTServer::handleBorrar);
        server.createContext("/api/arbol", BSTServer::handleGetArbol);

        server.setExecutor(null);
        server.start();
        System.out.println("===========================================");
        System.out.println(" Servidor BST corriendo en puerto " + port);
        System.out.println(" Abre index.html en tu navegador");
        System.out.println("===========================================");
    }

    static void addCors(HttpExchange ex) throws IOException {
        ex.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        ex.getResponseHeaders().add("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
        ex.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        ex.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
    }

    static void sendJson(HttpExchange ex, String json) throws IOException {
        addCors(ex);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        ex.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }

    static String readBody(HttpExchange ex) throws IOException {
        try (InputStream is = ex.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    static String getParam(HttpExchange ex, String key) {
        String query = ex.getRequestURI().getQuery();
        if (query == null)
            return null;
        for (String p : query.split("&")) {
            String[] kv = p.split("=");
            if (kv.length == 2 && kv[0].equals(key))
                return kv[1];
        }
        return null;
    }

    static String arbolJson() {
        return "{\"arbol\":" + arbol.toJson() + "}";
    }

    static void handleEstaVacio(HttpExchange ex) throws IOException {
        if (ex.getRequestMethod().equals("OPTIONS")) {
            addCors(ex);
            ex.sendResponseHeaders(204, -1);
            return;
        }
        boolean vacio = arbol.estaVacio();
        sendJson(ex, "{\"resultado\":" + vacio + ",\"mensaje\":\"El árbol " + (vacio ? "ESTÁ vacío" : "NO está vacío")
                + "\",\"arbol\":" + arbol.toJson() + "}");
    }

    static void handleAgregar(HttpExchange ex) throws IOException {
        if (ex.getRequestMethod().equals("OPTIONS")) {
            addCors(ex);
            ex.sendResponseHeaders(204, -1);
            return;
        }
        String val = getParam(ex, "valor");
        if (val == null) {
            sendJson(ex, "{\"error\":\"Falta parametro valor\"}");
            return;
        }
        try {
            int v = Integer.parseInt(val.trim());
            boolean agregado = arbol.agregar(v);
            String msg = agregado ? "Nodo " + v + " agregado exitosamente" : "El valor " + v + " ya existe en el árbol";
            sendJson(ex,
                    "{\"resultado\":" + agregado + ",\"mensaje\":\"" + msg + "\",\"arbol\":" + arbol.toJson() + "}");
        } catch (NumberFormatException e) {
            sendJson(ex, "{\"error\":\"Valor no es un número entero\"}");
        }
    }

    static void handleEliminar(HttpExchange ex) throws IOException {
        if (ex.getRequestMethod().equals("OPTIONS")) {
            addCors(ex);
            ex.sendResponseHeaders(204, -1);
            return;
        }
        String val = getParam(ex, "valor");
        if (val == null) {
            sendJson(ex, "{\"error\":\"Falta parametro valor\"}");
            return;
        }
        try {
            int v = Integer.parseInt(val.trim());
            boolean eliminado = arbol.eliminar(v);
            String msg = eliminado ? "Nodo " + v + " eliminado exitosamente"
                    : "El valor " + v + " no existe en el árbol";
            sendJson(ex,
                    "{\"resultado\":" + eliminado + ",\"mensaje\":\"" + msg + "\",\"arbol\":" + arbol.toJson() + "}");
        } catch (NumberFormatException e) {
            sendJson(ex, "{\"error\":\"Valor no es un número entero\"}");
        }
    }

    static void handleExiste(HttpExchange ex) throws IOException {
        if (ex.getRequestMethod().equals("OPTIONS")) {
            addCors(ex);
            ex.sendResponseHeaders(204, -1);
            return;
        }
        String val = getParam(ex, "valor");
        if (val == null) {
            sendJson(ex, "{\"error\":\"Falta parametro valor\"}");
            return;
        }
        try {
            int v = Integer.parseInt(val.trim());
            boolean existe = arbol.existe(v);
            sendJson(ex, "{\"resultado\":" + existe + ",\"mensaje\":\"El valor " + v
                    + (existe ? " SÍ existe" : " NO existe") + " en el árbol\",\"arbol\":" + arbol.toJson() + "}");
        } catch (NumberFormatException e) {
            sendJson(ex, "{\"error\":\"Valor no es un número entero\"}");
        }
    }

    static void handleRecorrer(HttpExchange ex) throws IOException {
        if (ex.getRequestMethod().equals("OPTIONS")) {
            addCors(ex);
            ex.sendResponseHeaders(204, -1);
            return;
        }
        String tipo = getParam(ex, "tipo");
        if (tipo == null)
            tipo = "inorden";
        List<Integer> lista = new ArrayList<>();
        switch (tipo.toLowerCase()) {
            case "inorden":
                arbol.inorden(arbol.raiz, lista);
                break;
            case "preorden":
                arbol.preorden(arbol.raiz, lista);
                break;
            case "postorden":
                arbol.postorden(arbol.raiz, lista);
                break;
            default:
                arbol.inorden(arbol.raiz, lista);
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < lista.size(); i++) {
            if (i > 0)
                sb.append(",");
            sb.append(lista.get(i));
        }
        sb.append("]");
        String tipoMostrar = tipo.substring(0, 1).toUpperCase() + tipo.substring(1).toLowerCase();
        sendJson(ex, "{\"resultado\":" + sb + ",\"mensaje\":\"Recorrido " + tipoMostrar + ": " + sb + "\",\"tipo\":\""
                + tipo + "\",\"arbol\":" + arbol.toJson() + "}");
    }

    static void handlePeso(HttpExchange ex) throws IOException {
        if (ex.getRequestMethod().equals("OPTIONS")) {
            addCors(ex);
            ex.sendResponseHeaders(204, -1);
            return;
        }
        int peso = arbol.obtenerPeso();
        sendJson(ex, "{\"resultado\":" + peso + ",\"mensaje\":\"El peso del árbol es " + peso + " nodo(s)\",\"arbol\":"
                + arbol.toJson() + "}");
    }

    static void handleAltura(HttpExchange ex) throws IOException {
        if (ex.getRequestMethod().equals("OPTIONS")) {
            addCors(ex);
            ex.sendResponseHeaders(204, -1);
            return;
        }
        int altura = arbol.obtenerAltura();
        sendJson(ex, "{\"resultado\":" + altura + ",\"mensaje\":\"La altura del árbol es " + altura + "\",\"arbol\":"
                + arbol.toJson() + "}");
    }

    static void handleNivel(HttpExchange ex) throws IOException {
        if (ex.getRequestMethod().equals("OPTIONS")) {
            addCors(ex);
            ex.sendResponseHeaders(204, -1);
            return;
        }
        String val = getParam(ex, "valor");
        if (val == null) {
            sendJson(ex, "{\"error\":\"Falta parametro valor\"}");
            return;
        }
        try {
            int v = Integer.parseInt(val.trim());
            int nivel = arbol.obtenerNivel(v);
            String msg = nivel >= 0 ? "El nodo " + v + " está en el nivel " + nivel
                    : "El nodo " + v + " no existe en el árbol";
            sendJson(ex, "{\"resultado\":" + nivel + ",\"mensaje\":\"" + msg + "\",\"arbol\":" + arbol.toJson() + "}");
        } catch (NumberFormatException e) {
            sendJson(ex, "{\"error\":\"Valor no es un número entero\"}");
        }
    }

    static void handleHojas(HttpExchange ex) throws IOException {
        if (ex.getRequestMethod().equals("OPTIONS")) {
            addCors(ex);
            ex.sendResponseHeaders(204, -1);
            return;
        }
        int hojas = arbol.contarHojas();
        sendJson(ex, "{\"resultado\":" + hojas + ",\"mensaje\":\"El árbol tiene " + hojas + " hoja(s)\",\"arbol\":"
                + arbol.toJson() + "}");
    }

    static void handleMenor(HttpExchange ex) throws IOException {
        if (ex.getRequestMethod().equals("OPTIONS")) {
            addCors(ex);
            ex.sendResponseHeaders(204, -1);
            return;
        }
        if (arbol.estaVacio()) {
            sendJson(ex, "{\"resultado\":null,\"mensaje\":\"El árbol está vacío\",\"arbol\":" + arbol.toJson() + "}");
            return;
        }
        int menor = arbol.obtenerMenor();
        sendJson(ex, "{\"resultado\":" + menor + ",\"mensaje\":\"El nodo menor es: " + menor + "\",\"arbol\":"
                + arbol.toJson() + "}");
    }

    static void handleMayor(HttpExchange ex) throws IOException {
        if (ex.getRequestMethod().equals("OPTIONS")) {
            addCors(ex);
            ex.sendResponseHeaders(204, -1);
            return;
        }
        if (arbol.estaVacio()) {
            sendJson(ex, "{\"resultado\":null,\"mensaje\":\"El árbol está vacío\",\"arbol\":" + arbol.toJson() + "}");
            return;
        }
        int mayor = arbol.obtenerMayor();
        sendJson(ex, "{\"resultado\":" + mayor + ",\"mensaje\":\"El nodo mayor es: " + mayor + "\",\"arbol\":"
                + arbol.toJson() + "}");
    }

    static void handleAmplitud(HttpExchange ex) throws IOException {
        if (ex.getRequestMethod().equals("OPTIONS")) {
            addCors(ex);
            ex.sendResponseHeaders(204, -1);
            return;
        }
        List<Integer> lista = arbol.imprimirAmplitud();
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < lista.size(); i++) {
            if (i > 0)
                sb.append(",");
            sb.append(lista.get(i));
        }
        sb.append("]");
        sendJson(ex, "{\"resultado\":" + sb + ",\"mensaje\":\"Recorrido por amplitud (BFS): " + sb + "\",\"arbol\":"
                + arbol.toJson() + "}");
    }

    static void handleBorrar(HttpExchange ex) throws IOException {
        if (ex.getRequestMethod().equals("OPTIONS")) {
            addCors(ex);
            ex.sendResponseHeaders(204, -1);
            return;
        }
        arbol.borrarArbol();
        sendJson(ex, "{\"resultado\":true,\"mensaje\":\"Árbol borrado completamente\",\"arbol\":null}");
    }

    static void handleGetArbol(HttpExchange ex) throws IOException {
        if (ex.getRequestMethod().equals("OPTIONS")) {
            addCors(ex);
            ex.sendResponseHeaders(204, -1);
            return;
        }
        sendJson(ex, "{\"arbol\":" + arbol.toJson() + "}");
    }
}
