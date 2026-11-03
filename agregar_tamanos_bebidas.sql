-- =====================================================================
-- TAMAÑOS PARA BEBIDAS Y CERVEZAS (350ml / 500ml / 1L)
-- PAPASAURIOS
--
-- Para bases de datos que ya existían antes de este cambio (si vas a
-- crear la base desde cero, no hace falta correr esto: papasauriosDb.sql
-- ya incluye los tamaños).
--
-- Ejecutar este script DESPUÉS de papasauriosDb.sql.
-- =====================================================================

-- 'Coca Cola 1.5 L' pasa a llamarse solo 'Coca Cola' porque ahora el
-- litraje se elige como tamaño (350ml/500ml/1L) en vez de ir en el
-- nombre. precio_base pasa a ser el del tamaño más pequeño (350ml),
-- igual que en las demás categorías con variantes (Sandwich, Dorilocos,
-- etc.): es el precio que se ve en el menú antes de elegir tamaño.
UPDATE `papasauriosdb`.`Producto`
   SET `nombre_producto` = 'Coca Cola', `precio_base` = 3500
 WHERE `nombre_producto` = 'Coca Cola 1.5 L' AND `Categoria_idCategoria` = 16;

UPDATE `papasauriosdb`.`Producto`
   SET `precio_base` = 1500
 WHERE `nombre_producto` = 'Agua en botella' AND `Categoria_idCategoria` = 16;

-- Solo inserta las variantes si el producto todavía no tiene ninguna
-- (para poder correr este script más de una vez sin duplicar filas).
INSERT INTO `papasauriosdb`.`Producto_variante` (`nombre_variante`, `precio_variante`, `Producto_idProducto`)
SELECT '350ml', 1500, idProducto FROM `papasauriosdb`.`Producto`
 WHERE nombre_producto = 'Agua en botella' AND Categoria_idCategoria = 16
   AND idProducto NOT IN (SELECT Producto_idProducto FROM `papasauriosdb`.`Producto_variante`);
INSERT INTO `papasauriosdb`.`Producto_variante` (`nombre_variante`, `precio_variante`, `Producto_idProducto`)
SELECT '500ml', 2000, idProducto FROM `papasauriosdb`.`Producto`
 WHERE nombre_producto = 'Agua en botella' AND Categoria_idCategoria = 16
   AND idProducto NOT IN (SELECT Producto_idProducto FROM `papasauriosdb`.`Producto_variante` WHERE nombre_variante = '500ml');
INSERT INTO `papasauriosdb`.`Producto_variante` (`nombre_variante`, `precio_variante`, `Producto_idProducto`)
SELECT '1L', 3500, idProducto FROM `papasauriosdb`.`Producto`
 WHERE nombre_producto = 'Agua en botella' AND Categoria_idCategoria = 16
   AND idProducto NOT IN (SELECT Producto_idProducto FROM `papasauriosdb`.`Producto_variante` WHERE nombre_variante = '1L');

INSERT INTO `papasauriosdb`.`Producto_variante` (`nombre_variante`, `precio_variante`, `Producto_idProducto`)
SELECT '350ml', 3500, idProducto FROM `papasauriosdb`.`Producto`
 WHERE nombre_producto = 'Coca Cola' AND Categoria_idCategoria = 16
   AND idProducto NOT IN (SELECT Producto_idProducto FROM `papasauriosdb`.`Producto_variante` WHERE nombre_variante = '350ml');
INSERT INTO `papasauriosdb`.`Producto_variante` (`nombre_variante`, `precio_variante`, `Producto_idProducto`)
SELECT '500ml', 4500, idProducto FROM `papasauriosdb`.`Producto`
 WHERE nombre_producto = 'Coca Cola' AND Categoria_idCategoria = 16
   AND idProducto NOT IN (SELECT Producto_idProducto FROM `papasauriosdb`.`Producto_variante` WHERE nombre_variante = '500ml');
INSERT INTO `papasauriosdb`.`Producto_variante` (`nombre_variante`, `precio_variante`, `Producto_idProducto`)
SELECT '1L', 8000, idProducto FROM `papasauriosdb`.`Producto`
 WHERE nombre_producto = 'Coca Cola' AND Categoria_idCategoria = 16
   AND idProducto NOT IN (SELECT Producto_idProducto FROM `papasauriosdb`.`Producto_variante` WHERE nombre_variante = '1L');

INSERT INTO `papasauriosdb`.`Producto_variante` (`nombre_variante`, `precio_variante`, `Producto_idProducto`)
SELECT '350ml', 5000, idProducto FROM `papasauriosdb`.`Producto`
 WHERE nombre_producto = 'Club Colombia' AND Categoria_idCategoria = 16
   AND idProducto NOT IN (SELECT Producto_idProducto FROM `papasauriosdb`.`Producto_variante` WHERE nombre_variante = '350ml');
INSERT INTO `papasauriosdb`.`Producto_variante` (`nombre_variante`, `precio_variante`, `Producto_idProducto`)
SELECT '500ml', 6500, idProducto FROM `papasauriosdb`.`Producto`
 WHERE nombre_producto = 'Club Colombia' AND Categoria_idCategoria = 16
   AND idProducto NOT IN (SELECT Producto_idProducto FROM `papasauriosdb`.`Producto_variante` WHERE nombre_variante = '500ml');
INSERT INTO `papasauriosdb`.`Producto_variante` (`nombre_variante`, `precio_variante`, `Producto_idProducto`)
SELECT '1L', 11000, idProducto FROM `papasauriosdb`.`Producto`
 WHERE nombre_producto = 'Club Colombia' AND Categoria_idCategoria = 16
   AND idProducto NOT IN (SELECT Producto_idProducto FROM `papasauriosdb`.`Producto_variante` WHERE nombre_variante = '1L');

INSERT INTO `papasauriosdb`.`Producto_variante` (`nombre_variante`, `precio_variante`, `Producto_idProducto`)
SELECT '350ml', 6000, idProducto FROM `papasauriosdb`.`Producto`
 WHERE nombre_producto = 'Corona' AND Categoria_idCategoria = 16
   AND idProducto NOT IN (SELECT Producto_idProducto FROM `papasauriosdb`.`Producto_variante` WHERE nombre_variante = '350ml');
INSERT INTO `papasauriosdb`.`Producto_variante` (`nombre_variante`, `precio_variante`, `Producto_idProducto`)
SELECT '500ml', 7500, idProducto FROM `papasauriosdb`.`Producto`
 WHERE nombre_producto = 'Corona' AND Categoria_idCategoria = 16
   AND idProducto NOT IN (SELECT Producto_idProducto FROM `papasauriosdb`.`Producto_variante` WHERE nombre_variante = '500ml');
INSERT INTO `papasauriosdb`.`Producto_variante` (`nombre_variante`, `precio_variante`, `Producto_idProducto`)
SELECT '1L', 12500, idProducto FROM `papasauriosdb`.`Producto`
 WHERE nombre_producto = 'Corona' AND Categoria_idCategoria = 16
   AND idProducto NOT IN (SELECT Producto_idProducto FROM `papasauriosdb`.`Producto_variante` WHERE nombre_variante = '1L');

INSERT INTO `papasauriosdb`.`Producto_variante` (`nombre_variante`, `precio_variante`, `Producto_idProducto`)
SELECT '350ml', 7000, idProducto FROM `papasauriosdb`.`Producto`
 WHERE nombre_producto = '3 Cordilleras' AND Categoria_idCategoria = 16
   AND idProducto NOT IN (SELECT Producto_idProducto FROM `papasauriosdb`.`Producto_variante` WHERE nombre_variante = '350ml');
INSERT INTO `papasauriosdb`.`Producto_variante` (`nombre_variante`, `precio_variante`, `Producto_idProducto`)
SELECT '500ml', 8500, idProducto FROM `papasauriosdb`.`Producto`
 WHERE nombre_producto = '3 Cordilleras' AND Categoria_idCategoria = 16
   AND idProducto NOT IN (SELECT Producto_idProducto FROM `papasauriosdb`.`Producto_variante` WHERE nombre_variante = '500ml');
INSERT INTO `papasauriosdb`.`Producto_variante` (`nombre_variante`, `precio_variante`, `Producto_idProducto`)
SELECT '1L', 14000, idProducto FROM `papasauriosdb`.`Producto`
 WHERE nombre_producto = '3 Cordilleras' AND Categoria_idCategoria = 16
   AND idProducto NOT IN (SELECT Producto_idProducto FROM `papasauriosdb`.`Producto_variante` WHERE nombre_variante = '1L');
