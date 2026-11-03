-- =====================================================================
-- REORDENAR LAS SECCIONES DEL MENÚ - PAPASAURIOS
-- Patacones queda de primera (es la pestaña que se abre al entrar al
-- menú) y Papas Saurios de últimas. Solo hace falta si ya tienes la
-- base creada: en una instalación nueva, papasauriosDb.sql ya deja
-- este mismo orden.
-- =====================================================================

USE `papasauriosdb`;

UPDATE Categoria SET orden =  1 WHERE nombre_categoria = 'Patacones';
UPDATE Categoria SET orden =  2 WHERE nombre_categoria = 'Sandwich';
UPDATE Categoria SET orden =  3 WHERE nombre_categoria = 'Dino Burguer';
UPDATE Categoria SET orden =  4 WHERE nombre_categoria = 'Dino Dog';
UPDATE Categoria SET orden =  5 WHERE nombre_categoria = 'Combos';
UPDATE Categoria SET orden =  6 WHERE nombre_categoria = 'Entradas';
UPDATE Categoria SET orden =  7 WHERE nombre_categoria = 'Alitas BBQ';
UPDATE Categoria SET orden =  8 WHERE nombre_categoria = 'Dorilocos';
UPDATE Categoria SET orden =  9 WHERE nombre_categoria = 'Picadas';
UPDATE Categoria SET orden = 10 WHERE nombre_categoria = 'Malteadas y Sodas';
UPDATE Categoria SET orden = 11 WHERE nombre_categoria = 'Jugos y Limonadas';
UPDATE Categoria SET orden = 12 WHERE nombre_categoria = 'Bebidas y Cervezas';
UPDATE Categoria SET orden = 13 WHERE nombre_categoria = 'Papas, Salchichas y Quesos';
UPDATE Categoria SET orden = 14 WHERE nombre_categoria = 'Proteínas y Toppings';
UPDATE Categoria SET orden = 15 WHERE nombre_categoria = 'Salsas';
UPDATE Categoria SET orden = 16 WHERE nombre_categoria = 'Papas Saurios';

SELECT idCategoria, nombre_categoria, orden FROM Categoria ORDER BY orden;
