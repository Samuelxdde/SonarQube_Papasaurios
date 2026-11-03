/**
 * Configuración de Google Maps para Papasaurios.
 *
 * ¿QUÉ HAY QUE HACER PARA QUE FUNCIONE EL MAPA EN VIVO?
 * -------------------------------------------------------
 * 1. Entra a https://console.cloud.google.com/google/maps-apis
 * 2. Crea (o usa) un proyecto y activa estas 2 APIs:
 *      - "Maps JavaScript API"
 *      - "Directions API"
 * 3. Crea una API key y pégala abajo, reemplazando el texto
 *    "TU_API_KEY_AQUI".
 * 4. (Recomendado) Restringe esa key por dominio/HTTP referrer para
 *    que nadie más la use desde otro sitio.
 *
 * SIN API KEY el sitio sigue funcionando: en vez del mapa en vivo
 * embebido, tanto el cliente como el repartidor ven un botón que
 * abre la ubicación / la ruta directo en Google Maps (eso sí
 * funciona siempre, sin key, porque es solo un link).
 */
const GOOGLE_MAPS_API_KEY = "AIzaSyBRXAzUXSpptAvYghMVA24pCQ3cBOXwQcM";

/**
 * Ubicación fija del local (Papasaurios Soacha). Es el "punto de
 * partida" por defecto en todos los mapas del sitio: el centro inicial
 * del mapa de seguimiento del cliente y el origen de la ruta mientras
 * el repartidor todavía no ha activado su GPS en vivo.
 *
 * Coordenadas aproximadas del centro de Soacha, Cundinamarca (no un
 * geocode exacto de "DG 34 #13A-40"). Si en algún momento se tiene la
 * coordenada exacta del local, basta con reemplazar estos dos valores
 * — todo lo demás en el sitio los referencia desde aquí.
 */
const DIRECCION_TIENDA = "DG 34 #13A-40, Soacha, Colombia";
const UBICACION_TIENDA = { lat: 4.5793, lng: -74.2168 };

/**
 * Ciudad/país que se agrega en silencio a cualquier dirección de
 * entrega antes de pedírsela a Google (Directions, deep link de "Cómo
 * llegar", etc.), SIN modificar el texto que el cliente escribió ni lo
 * que ve guardado en su pedido.
 *
 * ¿Por qué? Un cliente normalmente escribe algo como "Cra 12 #34-56,
 * Barrio Centro" sin ciudad ni país — a Google eso le cuesta más
 * ubicarlo bien (o lo ubica en el lugar equivocado del mundo), y
 * direcciones así son también las que más fácil se prestan para
 * relleno/inválidas. Completarla siempre con ", Bogotá, Colombia" por
 * detrás hace que el geocode sea consistente y confiable sin pedirle
 * nada extra al cliente ni tocar lo que ya escribió.
 */
const CIUDAD_PAIS_ENTREGA = "Bogotá, Colombia";

function direccionParaMapa(direccionCruda) {
    if (!direccionCruda) return direccionCruda;
    // Si el cliente ya escribió "Bogotá" o "Soacha" (con o sin tilde) no
    // se agrega nada — solo se completa cuando la dirección no menciona
    // ninguna de las dos, que es el caso más común de que a Google le
    // cueste ubicarla bien.
    const yaTieneCiudad = /bogot|soacha/i.test(direccionCruda);
    return yaTieneCiudad ? direccionCruda : (direccionCruda + ", " + CIUDAD_PAIS_ENTREGA);
}

function mapsApiKeyConfigurada() {
    return typeof GOOGLE_MAPS_API_KEY === "string"
        && GOOGLE_MAPS_API_KEY.trim() !== ""
        && GOOGLE_MAPS_API_KEY !== "TU_API_KEY_AQUI";
}

/**
 * Carga dinámicamente el script de Google Maps JS (con Directions
 * incluido vía la librería "routes") y ejecuta el callback cuando
 * esté listo. Si ya se cargó antes, ejecuta el callback de una vez.
 */
function cargarGoogleMaps(callback) {
    if (window.google && window.google.maps) {
        callback();
        return;
    }
    const scriptId = "google-maps-script";
    if (document.getElementById(scriptId)) {
        document.getElementById(scriptId).addEventListener("load", callback);
        return;
    }
    const script = document.createElement("script");
    script.id = scriptId;
    script.src = "https://maps.googleapis.com/maps/api/js?key=" + GOOGLE_MAPS_API_KEY;
    script.async = true;
    script.onload = callback;
    document.head.appendChild(script);
}
