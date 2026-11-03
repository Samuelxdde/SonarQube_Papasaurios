-- =====================================================================
-- ACTUALIZACIÓN DE IMÁGENES DE PRODUCTOS - PAPASAURIOS
-- Fotos extraídas del menú/publicidad en PDF y guardadas en:
--   web/Vista/Imagenes/menu/
-- Ejecutar este script DESPUÉS de papasauriosDb.sql (los productos
-- deben existir primero, ya que se busca por nombre_producto).
--
-- Ruta usada en imagen_url: relativa a la raíz del sitio web, lista
-- para usar en <img src="..."> desde las vistas JSP
-- (Vista/Imagenes/menu/archivo.jpg). Ajusta el prefijo si tu despliegue
-- sirve las imágenes desde otra ruta.
-- =====================================================================

USE `papasauriosdb`;

-- ---------------------------------------------------------------
-- PAPAS SAURIOS (personajes propios de cada tamaño)
-- ---------------------------------------------------------------
UPDATE Producto SET imagen_url = '/Vista/Imagenes/menu/dino_saurio.jpg'   WHERE nombre_producto = 'Dino';
UPDATE Producto SET imagen_url = '/Vista/Imagenes/menu/mafesaurus.jpg'    WHERE nombre_producto = 'Mafesaurus';
UPDATE Producto SET imagen_url = '/Vista/Imagenes/menu/majosaurus.jpg'    WHERE nombre_producto = 'Majosaurus';

-- ---------------------------------------------------------------
-- PAPAS, SALCHICHAS Y QUESOS (el menú usa una sola foto genérica
-- de la papa cargada para representar todos estos adicionales)
-- ---------------------------------------------------------------
UPDATE Producto SET imagen_url = '/Vista/Imagenes/menu/papas_complementos.jpg'
WHERE nombre_producto IN (
  'Papa criolla', 'Papa cascos', 'Papa francesa artesanal',
  'Salchicha americana', 'Salchicha ranchera',
  'Mozzarella fundido', 'Cheddar fundido', '4 quesos fundidos'
);

-- ---------------------------------------------------------------
-- PROTEÍNAS Y TOPPINGS (mismo criterio: foto genérica del menú)
-- ---------------------------------------------------------------
UPDATE Producto SET imagen_url = '/Vista/Imagenes/menu/papas_complementos.jpg'
WHERE nombre_producto IN (
  'Chorizo Santa Rosano', 'Tocineta', 'Costilla desmechada',
  'Cerdo desmechado', 'Chicharrón', 'Pollo a la parrilla', 'Birria de res',
  'Huevo frito', 'Pico de gallo', 'Crema agria', 'Maduritas', 'Guacamole'
);

-- ---------------------------------------------------------------
-- SALSAS (el menú no trae foto propia; se deja la misma foto genérica)
-- ---------------------------------------------------------------
UPDATE Producto SET imagen_url = '/Vista/Imagenes/menu/papas_complementos.jpg'
WHERE nombre_producto IN (
  'Salsa Jurásica', 'Salsa B.B.Q.', 'Salsa Miel Mostaza', 'Salsa Verde',
  'Salsa Búfalo', 'Salsa de Ajo', 'Ketchup'
);

-- ---------------------------------------------------------------
-- COMBOS
-- ---------------------------------------------------------------
UPDATE Producto SET imagen_url = '/Vista/Imagenes/menu/combo_salchipapa.jpg'
WHERE nombre_producto = 'Combo Salchipapa + Entrada + Jarra de limonada';

-- ---------------------------------------------------------------
-- ENTRADAS (foto propia para cada una)
-- ---------------------------------------------------------------
UPDATE Producto SET imagen_url = '/Vista/Imagenes/menu/entrada_costillas.jpg'  WHERE nombre_producto = 'Costillas';
UPDATE Producto SET imagen_url = '/Vista/Imagenes/menu/entrada_chicharron.jpg' WHERE nombre_producto = 'Chicharrón' AND Categoria_idCategoria = 6;
UPDATE Producto SET imagen_url = '/Vista/Imagenes/menu/entrada_chunchullo.jpg' WHERE nombre_producto = 'Chunchullo';

-- ---------------------------------------------------------------
-- SANDWICH (foto propia para cada uno)
-- ---------------------------------------------------------------
UPDATE Producto SET imagen_url = '/Vista/Imagenes/menu/sandwich_carne.jpg'            WHERE nombre_producto = 'Sandwich Carne';
UPDATE Producto SET imagen_url = '/Vista/Imagenes/menu/sandwich_pollo.jpg'            WHERE nombre_producto = 'Sandwich Pollo';
UPDATE Producto SET imagen_url = '/Vista/Imagenes/menu/sandwich_pernil.jpg'           WHERE nombre_producto = 'Sandwich Pernil';
UPDATE Producto SET imagen_url = '/Vista/Imagenes/menu/sandwich_pollo_tocineta.jpg'   WHERE nombre_producto = 'Sandwich Pollo Tocineta';
UPDATE Producto SET imagen_url = '/Vista/Imagenes/menu/sandwich_mixto.jpg'            WHERE nombre_producto = 'Sandwich Mixto';
UPDATE Producto SET imagen_url = '/Vista/Imagenes/menu/sandwich_roastbeef.jpg'        WHERE nombre_producto = 'Sandwich Roast Beef';
UPDATE Producto SET imagen_url = '/Vista/Imagenes/menu/sandwich_pernil_roastbeef.jpg' WHERE nombre_producto = 'Sandwich Pernil y Roast Beef';
UPDATE Producto SET imagen_url = '/Vista/Imagenes/menu/sandwich_cordero.jpg'          WHERE nombre_producto = 'Sandwich Cordero';
UPDATE Producto SET imagen_url = '/Vista/Imagenes/menu/sandwich_atun.jpg'             WHERE nombre_producto = 'Sandwich Atún';
UPDATE Producto SET imagen_url = '/Vista/Imagenes/menu/sandwich_de_la_casa.jpg'       WHERE nombre_producto = 'Sandwich De la Casa';

-- ---------------------------------------------------------------
-- DINO BURGUER / MAFE BURGUER / MAJO BURGUER
-- ---------------------------------------------------------------
UPDATE Producto SET imagen_url = '/Vista/Imagenes/menu/dino_burguer.jpg' WHERE nombre_producto = 'Dino Burguer';
UPDATE Producto SET imagen_url = '/Vista/Imagenes/menu/mafe_burguer.jpg' WHERE nombre_producto = 'Mafe Burguer';
UPDATE Producto SET imagen_url = '/Vista/Imagenes/menu/majo_burguer.jpg' WHERE nombre_producto = 'Majo Burguer';

-- ---------------------------------------------------------------
-- DINO DOG / MAFE DOG / MAJO DOG
-- ---------------------------------------------------------------
UPDATE Producto SET imagen_url = '/Vista/Imagenes/menu/dino_dog.jpg' WHERE nombre_producto = 'Dino Dog';
UPDATE Producto SET imagen_url = '/Vista/Imagenes/menu/mafe_dog.jpg' WHERE nombre_producto = 'Mafe Dog';
UPDATE Producto SET imagen_url = '/Vista/Imagenes/menu/majo_dog.jpg' WHERE nombre_producto = 'Majo Dog';

-- ---------------------------------------------------------------
-- PATACONES (foto propia para cada uno)
-- ---------------------------------------------------------------
UPDATE Producto SET imagen_url = '/Vista/Imagenes/menu/patacon_costeno.jpg'  WHERE nombre_producto = 'Patacón Costeño';
UPDATE Producto SET imagen_url = '/Vista/Imagenes/menu/patacon_mixto.jpg'   WHERE nombre_producto = 'Patacón Mixto';
UPDATE Producto SET imagen_url = '/Vista/Imagenes/menu/patacon_marinero.jpg' WHERE nombre_producto = 'Patacón Marinero';
UPDATE Producto SET imagen_url = '/Vista/Imagenes/menu/patacon_mexicano.jpg' WHERE nombre_producto = 'Patacón Mexicano';
UPDATE Producto SET imagen_url = '/Vista/Imagenes/menu/patacon_paisa.jpg'   WHERE nombre_producto = 'Patacón Paisa';

-- ---------------------------------------------------------------
-- ALITAS BBQ (ícono propio de cada combo de alitas)
-- ---------------------------------------------------------------
UPDATE Producto SET imagen_url = '/Vista/Imagenes/menu/wings_dino.jpg' WHERE nombre_producto = 'Dino Wings';
UPDATE Producto SET imagen_url = '/Vista/Imagenes/menu/wings_mafe.jpg' WHERE nombre_producto = 'Mafe Wings';
UPDATE Producto SET imagen_url = '/Vista/Imagenes/menu/wings_majo.jpg' WHERE nombre_producto = 'Majo Wings';

-- ---------------------------------------------------------------
-- DORILOCOS (el menú usa una sola foto para tradicional y mixto)
-- ---------------------------------------------------------------
UPDATE Producto SET imagen_url = '/Vista/Imagenes/menu/dorilocos.jpg'
WHERE nombre_producto IN ('Doriloco Tradicional', 'Doriloco Mixto');

-- ---------------------------------------------------------------
-- PICADA JURÁSICA
-- ---------------------------------------------------------------
UPDATE Producto SET imagen_url = '/Vista/Imagenes/menu/picada_jurasica.jpg' WHERE nombre_producto = 'Picada Jurásica';

-- ---------------------------------------------------------------
-- MALTEADAS Y SODAS (foto genérica por tipo, el menú no trae
-- foto individual por sabor)
-- ---------------------------------------------------------------
UPDATE Producto SET imagen_url = '/Vista/Imagenes/menu/malteada.jpg'
WHERE nombre_producto IN ('Malteada Oreo', 'Malteada Fresa', 'Malteada Vainilla', 'Malteada Chocolate');

UPDATE Producto SET imagen_url = '/Vista/Imagenes/menu/sodas.jpg'
WHERE nombre_producto IN ('Soda Hierbabuena', 'Soda Cereza', 'Soda Maracuyá', 'Soda Limón', 'Soda Arándano');

-- ---------------------------------------------------------------
-- JUGOS Y LIMONADAS (foto genérica del menú)
-- ---------------------------------------------------------------
UPDATE Producto SET imagen_url = '/Vista/Imagenes/menu/jugos_limonadas.jpg'
WHERE nombre_producto IN (
  'Jugo natural en agua', 'Jugo natural en leche',
  'Limonada Natural', 'Limonada Cerezada', 'Limonada Hierbabuena',
  'Limonada de Coco', 'Jarra de Limonada'
);

-- ---------------------------------------------------------------
-- BEBIDAS Y CERVEZAS (foto genérica del menú)
-- ---------------------------------------------------------------
UPDATE Producto SET imagen_url = '/Vista/Imagenes/menu/bebidas_cervezas.jpg'
WHERE nombre_producto IN (
  'Agua en botella', 'Coca Cola 1.5 L', 'Club Colombia', 'Corona', '3 Cordilleras'
);

-- ---------------------------------------------------------------
-- Verificación rápida: productos que quedaron sin imagen asignada
-- ---------------------------------------------------------------
-- SELECT idProducto, nombre_producto FROM Producto WHERE imagen_url IS NULL;
