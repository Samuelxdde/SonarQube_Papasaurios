drop database if exists papasauriosdb;

CREATE DATABASE IF NOT EXISTS `papasauriosdb` DEFAULT CHARACTER SET utf8 ;
USE `papasauriosdb` ;

-- -----------------------------------------------------
-- Table `papasauriosdb`.`Tipo_documento`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `papasauriosdb`.`Tipo_documento` (
  `idTipo_documento` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `descripcion_doc` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idTipo_documento`)
  );


-- -----------------------------------------------------
-- Table `papasauriosdb`.`Roles`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `papasauriosdb`.`Roles` (
  `idRoles` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `descripcion_rol` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idRoles`)
  );


-- -----------------------------------------------------
-- Table `papasauriosdb`.`Usuarios`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `papasauriosdb`.`Usuarios` (
  `idUsuarios` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NOT NULL,
  `apellido` VARCHAR(45) NOT NULL,
  `documento` VARCHAR(45) NOT NULL,
  `telefono` VARCHAR(45) NOT NULL,
  `correo` VARCHAR(45) NOT NULL,
  `clave` VARCHAR(255) NOT NULL,
  `fecha_nac` DATE NOT NULL,
  `fecha_cad` DATE NOT NULL,
  `checkbox` BOOLEAN NOT NULL,
  `Tipo_documento_idTipo_documento` INT UNSIGNED NOT NULL,
  `Roles_idRoles` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`idUsuarios`),
  UNIQUE INDEX `documento_UNIQUE` (`documento` ASC),
  CONSTRAINT `fk_Usuarios_Tipo_documento`
    FOREIGN KEY (`Tipo_documento_idTipo_documento`)
    REFERENCES `papasauriosdb`.`Tipo_documento` (`idTipo_documento`),
  CONSTRAINT `fk_Usuarios_Roles1`
    FOREIGN KEY (`Roles_idRoles`)
    REFERENCES `papasauriosdb`.`Roles` (`idRoles`)
);


-- -----------------------------------------------------
-- Table `papasauriosdb`.`Estado_pedido`
-- Reemplaza a Estado_reserva (Recibido, En preparación,
-- Listo para recoger/entregar, Entregado, Cancelado).
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `papasauriosdb`.`Estado_pedido` (
  `idEstado_pedido` INT NOT NULL AUTO_INCREMENT,
  `descripcion_esta` VARCHAR(45) NULL,
  PRIMARY KEY (`idEstado_pedido`),
  UNIQUE INDEX `idEstado_pedido_UNIQUE` (`idEstado_pedido` ASC)
  );


-- -----------------------------------------------------
-- Table `papasauriosdb`.`Pagos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `papasauriosdb`.`Pagos` (
  `idPagos` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `estado_pago` VARCHAR(45) NULL,
  PRIMARY KEY (`idPagos`),
  UNIQUE INDEX `idPagos_UNIQUE` (`idPagos` ASC)
  );


-- -----------------------------------------------------
-- Table `papasauriosdb`.`Categoria`
-- Las 13 secciones del menú (Papas Saurios, Sandwich,
-- Burguer, Dog, Patacones, Alitas BBQ, Dorilocos, etc).
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `papasauriosdb`.`Categoria` (
  `idCategoria` INT NOT NULL AUTO_INCREMENT,
  `nombre_categoria` VARCHAR(100) NOT NULL,
  `orden` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`idCategoria`),
  UNIQUE INDEX `idCategoria_UNIQUE` (`idCategoria` ASC)
  );


-- -----------------------------------------------------
-- Table `papasauriosdb`.`Producto`
-- Cada plato del menú. precio_base es el precio por defecto
-- (p.ej. tamaño único); cuando el producto tiene variantes de
-- tamaño/combo, esos precios viven en Producto_variante.
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `papasauriosdb`.`Producto` (
  `idProducto` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `nombre_producto` VARCHAR(150) NOT NULL,
  `descripcion_producto` VARCHAR(255) NULL,
  `precio_base` INT NOT NULL,
  `disponible` BOOLEAN NOT NULL DEFAULT TRUE,
  `Categoria_idCategoria` INT NOT NULL,
  `imagen_url` VARCHAR(500) NULL,
  PRIMARY KEY (`idProducto`),
  INDEX `fk_Producto_Categoria1_idx` (`Categoria_idCategoria` ASC),
  CONSTRAINT `fk_Producto_Categoria1`
    FOREIGN KEY (`Categoria_idCategoria`)
    REFERENCES `papasauriosdb`.`Categoria` (`idCategoria`)
  );


-- -----------------------------------------------------
-- Table `papasauriosdb`.`Producto_variante`
-- Variantes de un mismo producto con precio distinto
-- (ej. Sandwich: Pequeño / Combo Pequeño / Grande / Combo Grande;
-- Dorilocos: Mini / Grande; Alitas: Dino/Mafe/Majo Wings).
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `papasauriosdb`.`Producto_variante` (
  `idVariante` INT NOT NULL AUTO_INCREMENT,
  `nombre_variante` VARCHAR(100) NOT NULL,
  `precio_variante` INT NOT NULL,
  `Producto_idProducto` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`idVariante`),
  INDEX `fk_Producto_variante_Producto1_idx` (`Producto_idProducto` ASC),
  CONSTRAINT `fk_Producto_variante_Producto1`
    FOREIGN KEY (`Producto_idProducto`)
    REFERENCES `papasauriosdb`.`Producto` (`idProducto`)
  );


-- -----------------------------------------------------
-- Table `papasauriosdb`.`Pedido`
-- Reemplaza a Reserva. tipo_entrega: 'Recoger en tienda' o
-- 'Domicilio'. direccion_entrega solo aplica si es domicilio.
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `papasauriosdb`.`Pedido` (
  `idPedido` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `fecha` DATE NOT NULL,
  `hora` TIME NOT NULL,
  `hora_estimada` TIME NULL,
  `tipo_entrega` VARCHAR(45) NOT NULL,
  `direccion_entrega` VARCHAR(200) NULL,
  `total` INT NOT NULL,
  `Usuarios_idUsuarios` INT UNSIGNED NOT NULL,
  `Estado_pedido_idEstado_pedido` INT NOT NULL,
  `Pagos_idPagos` INT UNSIGNED NOT NULL,
  `Repartidor_idUsuarios` INT UNSIGNED NULL,
  PRIMARY KEY (`idPedido`),
  UNIQUE INDEX `idPedido_UNIQUE` (`idPedido` ASC),
  CONSTRAINT `fk_Pedido_Usuarios1`
    FOREIGN KEY (`Usuarios_idUsuarios`)
    REFERENCES `papasauriosdb`.`Usuarios` (`idUsuarios`),
  CONSTRAINT `fk_Pedido_Estado_pedido1`
    FOREIGN KEY (`Estado_pedido_idEstado_pedido`)
    REFERENCES `papasauriosdb`.`Estado_pedido` (`idEstado_pedido`),
  CONSTRAINT `fk_Pedido_Pagos1`
    FOREIGN KEY (`Pagos_idPagos`)
    REFERENCES `papasauriosdb`.`Pagos` (`idPagos`),
  CONSTRAINT `fk_Pedido_Repartidor1`
    FOREIGN KEY (`Repartidor_idUsuarios`)
    REFERENCES `papasauriosdb`.`Usuarios` (`idUsuarios`)
  );


-- -----------------------------------------------------
-- Table `papasauriosdb`.`Detalle_pedido`
-- Cada línea del carrito: qué producto/variante, cantidad,
-- y el precio unitario congelado al momento de pedir (para
-- que cambios futuros de precio no alteren pedidos pasados).
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `papasauriosdb`.`Detalle_pedido` (
  `idDetalle` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `cantidad` INT NOT NULL,
  `precio_unitario` INT NOT NULL,
  `nota` VARCHAR(200) NULL,
  `Pedido_idPedido` INT UNSIGNED NOT NULL,
  `Producto_idProducto` INT UNSIGNED NOT NULL,
  `Producto_variante_idVariante` INT NULL,
  PRIMARY KEY (`idDetalle`),
  INDEX `fk_Detalle_Pedido1_idx` (`Pedido_idPedido` ASC),
  INDEX `fk_Detalle_Producto1_idx` (`Producto_idProducto` ASC),
  CONSTRAINT `fk_Detalle_Pedido1`
    FOREIGN KEY (`Pedido_idPedido`)
    REFERENCES `papasauriosdb`.`Pedido` (`idPedido`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_Detalle_Producto1`
    FOREIGN KEY (`Producto_idProducto`)
    REFERENCES `papasauriosdb`.`Producto` (`idProducto`),
  CONSTRAINT `fk_Detalle_Variante1`
    FOREIGN KEY (`Producto_variante_idVariante`)
    REFERENCES `papasauriosdb`.`Producto_variante` (`idVariante`)
  );


-- -----------------------------------------------------
-- Ubicacion_pedido
-- Última posición GPS reportada por el repartidor mientras
-- lleva un pedido a domicilio. Una fila por pedido (se pisa con
-- cada actualización, no se guarda historial): es lo que consulta
-- la página de seguimiento del cliente para dibujar al repartidor
-- en el mapa en tiempo (casi) real.
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `papasauriosdb`.`Ubicacion_pedido` (
  `Pedido_idPedido` INT UNSIGNED NOT NULL,
  `latitud` DOUBLE NOT NULL,
  `longitud` DOUBLE NOT NULL,
  `actualizado` DATETIME NOT NULL,
  PRIMARY KEY (`Pedido_idPedido`),
  CONSTRAINT `fk_Ubicacion_Pedido1`
    FOREIGN KEY (`Pedido_idPedido`)
    REFERENCES `papasauriosdb`.`Pedido` (`idPedido`)
    ON DELETE CASCADE
  );


-- =======================================================
-- DATOS INICIALES
-- =======================================================

INSERT INTO `papasauriosdb`.`Estado_pedido` (`descripcion_esta`) VALUES
  ('Recibido'),
  ('En preparación'),
  ('Listo'),
  ('Entregado'),
  ('Cancelado');

INSERT INTO `papasauriosdb`.`Pagos` (`estado_pago`) VALUES
  ('Pendiente de pago'),
  ('Pagado');

INSERT INTO `papasauriosdb`.`Tipo_documento` (`descripcion_doc`) VALUES
  ('Cédula de Ciudadanía'),
  ('Tarjeta de Identidad'),
  ('Cédula de Extranjería');

-- idRoles 1 = Administrador, 2 = Usuario (cliente), 3 = Repartidor.
-- El id 3 queda fijo porque InicioSesion.java y las vistas de admin
-- reconocen el rol de repartidor por este id.
-- idRoles 1 = Administrador, 2 = Usuario (cliente), 3 = Repartidor, 4 = Cocina.
-- Los id 3 y 4 quedan fijos porque InicioSesion.java y el filtro de
-- roles los reconocen por este número.
INSERT INTO `papasauriosdb`.`Roles` (`descripcion_rol`) VALUES
  ('Administrador'),
  ('Usuario'),
  ('Repartidor'),
  ('Cocina');

INSERT INTO `papasauriosdb`.`Usuarios`
  (`nombre`,`apellido`,`documento`,`telefono`,`correo`,`clave`,`fecha_nac`,`fecha_cad`,`checkbox`,`Tipo_documento_idTipo_documento`,`Roles_idRoles`)
VALUES
  ('Admin','Papasaurios','111','3000000000','contacto@papasaurios.com','admin123','1990-01-01','2030-01-01',1,1,1),
  ('Rex','Repartidor','222','3000000001','repartidor@papasaurios.com','repartidor123','1995-01-01','2030-01-01',1,1,3),
  ('Coco','Cocina','333','3000000002','cocina@papasaurios.com','cocina123','1992-01-01','2030-01-01',1,1,4);

-- ---------------------------------------------------------------
-- CATEGORÍAS (las 13 secciones del menú)
-- ---------------------------------------------------------------
INSERT INTO `papasauriosdb`.`Categoria` (`nombre_categoria`, `orden`) VALUES
  ('Patacones', 1),
  ('Sandwich', 2),
  ('Dino Burguer', 3),
  ('Dino Dog', 4),
  ('Combos', 5),
  ('Entradas', 6),
  ('Alitas BBQ', 7),
  ('Dorilocos', 8),
  ('Picadas', 9),
  ('Malteadas y Sodas', 10),
  ('Jugos y Limonadas', 11),
  ('Bebidas y Cervezas', 12),
  ('Papas, Salchichas y Quesos', 13),
  ('Proteínas y Toppings', 14),
  ('Salsas', 15),
  ('Papas Saurios', 16);

-- ---------------------------------------------------------------
-- PAPAS SAURIOS (armables, con variantes por número de salsas/proteínas)
-- ---------------------------------------------------------------
INSERT INTO `papasauriosdb`.`Producto` (`nombre_producto`, `descripcion_producto`, `precio_base`, `Categoria_idCategoria`) VALUES
  ('Dino', 'Papa criolla/cascos/francesa artesanal, queso cheddar o mozzarella, salchicha americana o ranchera, 1 proteína a elección, 2 toppings a elección', 32000, 1),
  ('Mafesaurus', 'Papa criolla/cascos/francesa artesanal, queso cheddar o mozzarella, salchicha americana o ranchera, 2 proteínas a elección, 3 toppings a elección', 34000, 1),
  ('Majosaurus', 'Papa criolla/cascos/francesa artesanal, queso cheddar o mozzarella, salchicha americana o ranchera, 4 proteínas a elección, 5 toppings a elección', 39000, 1);

INSERT INTO `papasauriosdb`.`Producto_variante` (`nombre_variante`, `precio_variante`, `Producto_idProducto`) VALUES
  ('x1 (2 salsas)', 32000, (SELECT idProducto FROM Producto WHERE nombre_producto='Dino')),
  ('x2 (3 salsas)', 48000, (SELECT idProducto FROM Producto WHERE nombre_producto='Dino')),
  ('x4 (4 salsas)', 87000, (SELECT idProducto FROM Producto WHERE nombre_producto='Dino')),
  ('x1 (2 salsas)', 34000, (SELECT idProducto FROM Producto WHERE nombre_producto='Mafesaurus')),
  ('x2 (3 salsas)', 53000, (SELECT idProducto FROM Producto WHERE nombre_producto='Mafesaurus')),
  ('x4 (4 salsas)', 94000, (SELECT idProducto FROM Producto WHERE nombre_producto='Mafesaurus')),
  ('x1 (2 salsas)', 39000, (SELECT idProducto FROM Producto WHERE nombre_producto='Majosaurus')),
  ('x2 (3 salsas)', 61000, (SELECT idProducto FROM Producto WHERE nombre_producto='Majosaurus')),
  ('x4 (5 salsas)', 108000, (SELECT idProducto FROM Producto WHERE nombre_producto='Majosaurus'));

-- ---------------------------------------------------------------
-- PAPAS, SALCHICHAS Y QUESOS (complementos sueltos)
-- ---------------------------------------------------------------
INSERT INTO `papasauriosdb`.`Producto` (`nombre_producto`, `descripcion_producto`, `precio_base`, `Categoria_idCategoria`) VALUES
  ('Papa criolla', 'Porción adicional', 9000, 2),
  ('Papa cascos', 'Porción adicional', 9000, 2),
  ('Papa francesa artesanal', 'Porción adicional', 9000, 2),
  ('Salchicha americana', 'Adicional', 8000, 2),
  ('Salchicha ranchera', 'Adicional', 10000, 2),
  ('Mozzarella fundido', 'Adicional', 9000, 2),
  ('Cheddar fundido', 'Adicional', 9000, 2),
  ('4 quesos fundidos', 'Adicional', 16000, 2);

-- ---------------------------------------------------------------
-- PROTEÍNAS Y TOPPINGS
-- ---------------------------------------------------------------
INSERT INTO `papasauriosdb`.`Producto` (`nombre_producto`, `descripcion_producto`, `precio_base`, `Categoria_idCategoria`) VALUES
  ('Chorizo Santa Rosano', 'Proteína adicional', 9000, 3),
  ('Tocineta', 'Proteína adicional', 10000, 3),
  ('Costilla desmechada', 'Proteína adicional', 12000, 3),
  ('Cerdo desmechado', 'Proteína adicional', 12000, 3),
  ('Chicharrón', 'Proteína adicional', 13000, 3),
  ('Pollo a la parrilla', 'Proteína adicional', 13000, 3),
  ('Birria de res', 'Proteína adicional', 15000, 3),
  ('Huevo frito', 'Topping adicional', 2000, 3),
  ('Pico de gallo', 'Topping adicional', 4500, 3),
  ('Crema agria', 'Topping adicional', 5500, 3),
  ('Maduritas', 'Topping adicional', 7000, 3),
  ('Guacamole', 'Topping adicional', 8500, 3);

-- ---------------------------------------------------------------
-- SALSAS
-- ---------------------------------------------------------------
INSERT INTO `papasauriosdb`.`Producto` (`nombre_producto`, `descripcion_producto`, `precio_base`, `Categoria_idCategoria`) VALUES
  ('Salsa Jurásica', 'Salsa de la casa', 2500, 4),
  ('Salsa B.B.Q.', 'Salsa de la casa', 2500, 4),
  ('Salsa Miel Mostaza', 'Salsa de la casa', 2500, 4),
  ('Salsa Verde', 'Salsa de la casa', 2500, 4),
  ('Salsa Búfalo', 'Salsa de la casa', 2500, 4),
  ('Salsa de Ajo', 'Salsa de la casa', 2500, 4),
  ('Ketchup', 'Salsa de la casa', 2500, 4);

-- ---------------------------------------------------------------
-- COMBOS
-- ---------------------------------------------------------------
INSERT INTO `papasauriosdb`.`Producto` (`nombre_producto`, `descripcion_producto`, `precio_base`, `Categoria_idCategoria`) VALUES
  ('Combo Salchipapa + Entrada + Jarra de limonada', 'Salchipapa, entrada gratis y jarra de limonada de regalo', 45000, 5);

-- ---------------------------------------------------------------
-- ENTRADAS
-- ---------------------------------------------------------------
INSERT INTO `papasauriosdb`.`Producto` (`nombre_producto`, `descripcion_producto`, `precio_base`, `Categoria_idCategoria`) VALUES
  ('Costillas', 'Entrada de costillas BBQ con papas y guacamole', 25000, 6),
  ('Chicharrón', 'Entrada de chicharrón con patacones y guacamole', 25000, 6),
  ('Chunchullo', 'Entrada de chunchullo con papas criollas y limón', 25000, 6);

-- ---------------------------------------------------------------
-- SANDWICH (con 4 variantes de tamaño/combo cada uno)
-- ---------------------------------------------------------------
INSERT INTO `papasauriosdb`.`Producto` (`nombre_producto`, `descripcion_producto`, `precio_base`, `Categoria_idCategoria`) VALUES
  ('Sandwich Carne', 'Carne desmechada, tocineta, pimentón', 18900, 7),
  ('Sandwich Pollo', 'Pechuga desmechada', 18400, 7),
  ('Sandwich Pernil', 'Pernil de cerdo ahumado', 18900, 7),
  ('Sandwich Pollo Tocineta', 'Pechuga desmechada, tocineta, queso cheddar', 18900, 7),
  ('Sandwich Mixto', 'Pechuga desmechada, carne desmechada', 19500, 7),
  ('Sandwich Roast Beef', 'Carne de res horneada', 19900, 7),
  ('Sandwich Pernil y Roast Beef', 'Carne de res horneada, pernil de cerdo ahumado', 19900, 7),
  ('Sandwich Cordero', 'Jamón de cordero', 18900, 7),
  ('Sandwich Atún', 'Lomitos de atún', 18400, 7),
  ('Sandwich De la Casa', 'Cerdo pulled, jamón de cordero, tocineta, pepinillos, queso cheddar', 18900, 7);

INSERT INTO `papasauriosdb`.`Producto_variante` (`nombre_variante`, `precio_variante`, `Producto_idProducto`) VALUES
  ('Pequeño', 18900, (SELECT idProducto FROM Producto WHERE nombre_producto='Sandwich Carne')),
  ('Combo Pequeño', 25900, (SELECT idProducto FROM Producto WHERE nombre_producto='Sandwich Carne')),
  ('Grande', 26900, (SELECT idProducto FROM Producto WHERE nombre_producto='Sandwich Carne')),
  ('Combo Grande', 33900, (SELECT idProducto FROM Producto WHERE nombre_producto='Sandwich Carne')),

  ('Pequeño', 18400, (SELECT idProducto FROM Producto WHERE nombre_producto='Sandwich Pollo')),
  ('Combo Pequeño', 25400, (SELECT idProducto FROM Producto WHERE nombre_producto='Sandwich Pollo')),
  ('Grande', 26400, (SELECT idProducto FROM Producto WHERE nombre_producto='Sandwich Pollo')),
  ('Combo Grande', 33400, (SELECT idProducto FROM Producto WHERE nombre_producto='Sandwich Pollo')),

  ('Pequeño', 18900, (SELECT idProducto FROM Producto WHERE nombre_producto='Sandwich Pernil')),
  ('Combo Pequeño', 25900, (SELECT idProducto FROM Producto WHERE nombre_producto='Sandwich Pernil')),
  ('Grande', 26900, (SELECT idProducto FROM Producto WHERE nombre_producto='Sandwich Pernil')),
  ('Combo Grande', 33900, (SELECT idProducto FROM Producto WHERE nombre_producto='Sandwich Pernil')),

  ('Pequeño', 18900, (SELECT idProducto FROM Producto WHERE nombre_producto='Sandwich Pollo Tocineta')),
  ('Combo Pequeño', 25900, (SELECT idProducto FROM Producto WHERE nombre_producto='Sandwich Pollo Tocineta')),
  ('Grande', 26900, (SELECT idProducto FROM Producto WHERE nombre_producto='Sandwich Pollo Tocineta')),
  ('Combo Grande', 33900, (SELECT idProducto FROM Producto WHERE nombre_producto='Sandwich Pollo Tocineta')),

  ('Pequeño', 19500, (SELECT idProducto FROM Producto WHERE nombre_producto='Sandwich Mixto')),
  ('Combo Pequeño', 26500, (SELECT idProducto FROM Producto WHERE nombre_producto='Sandwich Mixto')),
  ('Grande', 27500, (SELECT idProducto FROM Producto WHERE nombre_producto='Sandwich Mixto')),
  ('Combo Grande', 34500, (SELECT idProducto FROM Producto WHERE nombre_producto='Sandwich Mixto')),

  ('Pequeño', 19900, (SELECT idProducto FROM Producto WHERE nombre_producto='Sandwich Roast Beef')),
  ('Combo Pequeño', 26900, (SELECT idProducto FROM Producto WHERE nombre_producto='Sandwich Roast Beef')),
  ('Grande', 27900, (SELECT idProducto FROM Producto WHERE nombre_producto='Sandwich Roast Beef')),
  ('Combo Grande', 34900, (SELECT idProducto FROM Producto WHERE nombre_producto='Sandwich Roast Beef')),

  ('Pequeño', 19900, (SELECT idProducto FROM Producto WHERE nombre_producto='Sandwich Pernil y Roast Beef')),
  ('Combo Pequeño', 26900, (SELECT idProducto FROM Producto WHERE nombre_producto='Sandwich Pernil y Roast Beef')),
  ('Grande', 27900, (SELECT idProducto FROM Producto WHERE nombre_producto='Sandwich Pernil y Roast Beef')),
  ('Combo Grande', 34900, (SELECT idProducto FROM Producto WHERE nombre_producto='Sandwich Pernil y Roast Beef')),

  ('Pequeño', 18900, (SELECT idProducto FROM Producto WHERE nombre_producto='Sandwich Cordero')),
  ('Combo Pequeño', 25900, (SELECT idProducto FROM Producto WHERE nombre_producto='Sandwich Cordero')),
  ('Grande', 26900, (SELECT idProducto FROM Producto WHERE nombre_producto='Sandwich Cordero')),
  ('Combo Grande', 33900, (SELECT idProducto FROM Producto WHERE nombre_producto='Sandwich Cordero')),

  ('Pequeño', 18400, (SELECT idProducto FROM Producto WHERE nombre_producto='Sandwich Atún')),
  ('Combo Pequeño', 25400, (SELECT idProducto FROM Producto WHERE nombre_producto='Sandwich Atún')),
  ('Grande', 26400, (SELECT idProducto FROM Producto WHERE nombre_producto='Sandwich Atún')),
  ('Combo Grande', 33400, (SELECT idProducto FROM Producto WHERE nombre_producto='Sandwich Atún')),

  ('Pequeño', 18900, (SELECT idProducto FROM Producto WHERE nombre_producto='Sandwich De la Casa')),
  ('Combo Pequeño', 25900, (SELECT idProducto FROM Producto WHERE nombre_producto='Sandwich De la Casa')),
  ('Grande', 26900, (SELECT idProducto FROM Producto WHERE nombre_producto='Sandwich De la Casa')),
  ('Combo Grande', 33900, (SELECT idProducto FROM Producto WHERE nombre_producto='Sandwich De la Casa'));

-- ---------------------------------------------------------------
-- DINO BURGUER (sola / combo)
-- ---------------------------------------------------------------
INSERT INTO `papasauriosdb`.`Producto` (`nombre_producto`, `descripcion_producto`, `precio_base`, `Categoria_idCategoria`) VALUES
  ('Dino Burguer', 'Carne 180gr, lechuga, tomate, cebolla, queso', 22000, 8),
  ('Mafe Burguer', 'Carne 180gr, lechuga, tomate, tocineta, cebolla, queso', 24000, 8),
  ('Majo Burguer', 'Doble carne 105gr, tocineta, aguacate, huevo, queso, tomate, cebolla, lechuga, bañada en queso fundido', 29000, 8);

INSERT INTO `papasauriosdb`.`Producto_variante` (`nombre_variante`, `precio_variante`, `Producto_idProducto`) VALUES
  ('Sola', 22000, (SELECT idProducto FROM Producto WHERE nombre_producto='Dino Burguer')),
  ('Combo', 29000, (SELECT idProducto FROM Producto WHERE nombre_producto='Dino Burguer')),
  ('Sola', 24000, (SELECT idProducto FROM Producto WHERE nombre_producto='Mafe Burguer')),
  ('Combo', 31000, (SELECT idProducto FROM Producto WHERE nombre_producto='Mafe Burguer')),
  ('Sola', 29000, (SELECT idProducto FROM Producto WHERE nombre_producto='Majo Burguer')),
  ('Combo', 36000, (SELECT idProducto FROM Producto WHERE nombre_producto='Majo Burguer'));

-- ---------------------------------------------------------------
-- DINO DOG (sola / combo)
-- ---------------------------------------------------------------
INSERT INTO `papasauriosdb`.`Producto` (`nombre_producto`, `descripcion_producto`, `precio_base`, `Categoria_idCategoria`) VALUES
  ('Dino Dog', 'Pan, salchicha americana, papa chip, queso, huevo de codorniz', 14000, 9),
  ('Mafe Dog', 'Pan, salchicha americana, papa chip, queso, huevo de codorniz, tocineta', 17000, 9),
  ('Majo Dog', 'Pan, salchicha americana, papa chip, queso, huevo de codorniz, tocineta, maíz tierno', 18000, 9);

INSERT INTO `papasauriosdb`.`Producto_variante` (`nombre_variante`, `precio_variante`, `Producto_idProducto`) VALUES
  ('Solo', 14000, (SELECT idProducto FROM Producto WHERE nombre_producto='Dino Dog')),
  ('Combo', 21000, (SELECT idProducto FROM Producto WHERE nombre_producto='Dino Dog')),
  ('Solo', 17000, (SELECT idProducto FROM Producto WHERE nombre_producto='Mafe Dog')),
  ('Combo', 24000, (SELECT idProducto FROM Producto WHERE nombre_producto='Mafe Dog')),
  ('Solo', 18000, (SELECT idProducto FROM Producto WHERE nombre_producto='Majo Dog')),
  ('Combo', 25000, (SELECT idProducto FROM Producto WHERE nombre_producto='Majo Dog'));

-- ---------------------------------------------------------------
-- PATACONES (todos al mismo precio según el menú)
-- ---------------------------------------------------------------
INSERT INTO `papasauriosdb`.`Producto` (`nombre_producto`, `descripcion_producto`, `precio_base`, `Categoria_idCategoria`) VALUES
  ('Patacón Costeño', 'Lechuga, carne desmechada, salchicha, butifarra, queso costeño, suero costeño', 32900, 10),
  ('Patacón Mixto', 'Lechuga, carne desmechada, pollo desmechado, maíz, tocineta, queso, huevo de codorniz', 32900, 10),
  ('Patacón Marinero', 'Lechuga, pulpo, calamares, camarones, queso parmesano, salsa marinera', 32900, 10),
  ('Patacón Mexicano', 'Lechuga, carne desmechada, frijol, guacamole, pico de gallo, crema agria, jalapeños, queso, totopos, huevo de codorniz', 32900, 10),
  ('Patacón Paisa', 'Lechuga, carne desmechada, frijol, chorizo, chicharrón, tocineta, queso, huevo de codorniz', 32900, 10);

-- ---------------------------------------------------------------
-- ALITAS BBQ (Dino / Mafe / Majo Wings)
-- ---------------------------------------------------------------
INSERT INTO `papasauriosdb`.`Producto` (`nombre_producto`, `descripcion_producto`, `precio_base`, `Categoria_idCategoria`) VALUES
  ('Dino Wings', '5 piezas de alas + papa francesa + apio + zanahoria + 1 salsa', 23900, 11),
  ('Mafe Wings', '10 piezas de alas + papa francesa + apio + zanahoria + 2 salsas', 38900, 11),
  ('Majo Wings', '20 piezas de alas + papa francesa + apio + zanahoria + 3 salsas + 1 jarra de limonada', 73900, 11);

-- ---------------------------------------------------------------
-- DORILOCOS (Tradicional / Mixto, Mini / Grande)
-- ---------------------------------------------------------------
INSERT INTO `papasauriosdb`.`Producto` (`nombre_producto`, `descripcion_producto`, `precio_base`, `Categoria_idCategoria`) VALUES
  ('Doriloco Tradicional', 'Carne o pollo desmechado, maíz tierno, salchicha, queso, salsas', 17000, 12),
  ('Doriloco Mixto', 'Carne y pollo desmechado, maíz tierno, salchicha, queso, salsas', 19500, 12);

INSERT INTO `papasauriosdb`.`Producto_variante` (`nombre_variante`, `precio_variante`, `Producto_idProducto`) VALUES
  ('Mini', 17000, (SELECT idProducto FROM Producto WHERE nombre_producto='Doriloco Tradicional')),
  ('Grande', 26500, (SELECT idProducto FROM Producto WHERE nombre_producto='Doriloco Tradicional')),
  ('Mini', 19500, (SELECT idProducto FROM Producto WHERE nombre_producto='Doriloco Mixto')),
  ('Grande', 28500, (SELECT idProducto FROM Producto WHERE nombre_producto='Doriloco Mixto'));

-- ---------------------------------------------------------------
-- PICADAS
-- ---------------------------------------------------------------
INSERT INTO `papasauriosdb`.`Producto` (`nombre_producto`, `descripcion_producto`, `precio_base`, `Categoria_idCategoria`) VALUES
  ('Picada Jurásica', 'Papa francesa y criolla, alitas, chicharrón, costillas, chunchullo y chorizo', 74000, 13);

-- ---------------------------------------------------------------
-- MALTEADAS Y SODAS
-- ---------------------------------------------------------------
INSERT INTO `papasauriosdb`.`Producto` (`nombre_producto`, `descripcion_producto`, `precio_base`, `Categoria_idCategoria`) VALUES
  ('Malteada Oreo', 'Malteada artesanal', 16000, 14),
  ('Malteada Fresa', 'Malteada artesanal', 16000, 14),
  ('Malteada Vainilla', 'Malteada artesanal', 16000, 14),
  ('Malteada Chocolate', 'Malteada artesanal', 16000, 14),
  ('Soda Hierbabuena', 'Soda de la casa', 12000, 14),
  ('Soda Cereza', 'Soda de la casa', 12000, 14),
  ('Soda Maracuyá', 'Soda de la casa', 12000, 14),
  ('Soda Limón', 'Soda de la casa', 12000, 14),
  ('Soda Arándano', 'Soda de la casa', 12000, 14);

-- ---------------------------------------------------------------
-- JUGOS Y LIMONADAS
-- ---------------------------------------------------------------
INSERT INTO `papasauriosdb`.`Producto` (`nombre_producto`, `descripcion_producto`, `precio_base`, `Categoria_idCategoria`) VALUES
  ('Jugo natural en agua', 'Mora, fresa, maracuyá, mango, lulo, guanábana, frutos rojos o amarillos', 6000, 15),
  ('Jugo natural en leche', 'Mora, fresa, maracuyá, mango, lulo, guanábana, frutos rojos o amarillos', 8000, 15),
  ('Limonada Natural', 'Limonada de la casa', 5000, 15),
  ('Limonada Cerezada', 'Limonada de la casa', 7000, 15),
  ('Limonada Hierbabuena', 'Limonada de la casa', 7000, 15),
  ('Limonada de Coco', 'Limonada de la casa', 9000, 15),
  ('Jarra de Limonada', 'Para compartir', 18000, 15);

-- ---------------------------------------------------------------
-- BEBIDAS Y CERVEZAS (con tamaños por volumen: 350ml / 500ml / 1L)
-- precio_base = precio del tamaño más pequeño (350ml), igual que en
-- las demás categorías con variantes: es el precio que se muestra por
-- defecto en el menú antes de elegir tamaño.
-- ---------------------------------------------------------------
INSERT INTO `papasauriosdb`.`Producto` (`nombre_producto`, `descripcion_producto`, `precio_base`, `Categoria_idCategoria`) VALUES
  ('Agua en botella', 'Bebida', 1500, 16),
  ('Coca Cola', 'Bebida', 3500, 16),
  ('Club Colombia', 'Cerveza', 5000, 16),
  ('Corona', 'Cerveza', 6000, 16),
  ('3 Cordilleras', 'Cerveza', 7000, 16);

INSERT INTO `papasauriosdb`.`Producto_variante` (`nombre_variante`, `precio_variante`, `Producto_idProducto`) VALUES
  ('350ml', 1500, (SELECT idProducto FROM Producto WHERE nombre_producto='Agua en botella')),
  ('500ml', 2000, (SELECT idProducto FROM Producto WHERE nombre_producto='Agua en botella')),
  ('1L', 3500, (SELECT idProducto FROM Producto WHERE nombre_producto='Agua en botella')),

  ('350ml', 3500, (SELECT idProducto FROM Producto WHERE nombre_producto='Coca Cola')),
  ('500ml', 4500, (SELECT idProducto FROM Producto WHERE nombre_producto='Coca Cola')),
  ('1L', 8000, (SELECT idProducto FROM Producto WHERE nombre_producto='Coca Cola')),

  ('350ml', 5000, (SELECT idProducto FROM Producto WHERE nombre_producto='Club Colombia')),
  ('500ml', 6500, (SELECT idProducto FROM Producto WHERE nombre_producto='Club Colombia')),
  ('1L', 11000, (SELECT idProducto FROM Producto WHERE nombre_producto='Club Colombia')),

  ('350ml', 6000, (SELECT idProducto FROM Producto WHERE nombre_producto='Corona')),
  ('500ml', 7500, (SELECT idProducto FROM Producto WHERE nombre_producto='Corona')),
  ('1L', 12500, (SELECT idProducto FROM Producto WHERE nombre_producto='Corona')),

  ('350ml', 7000, (SELECT idProducto FROM Producto WHERE nombre_producto='3 Cordilleras')),
  ('500ml', 8500, (SELECT idProducto FROM Producto WHERE nombre_producto='3 Cordilleras')),
  ('1L', 14000, (SELECT idProducto FROM Producto WHERE nombre_producto='3 Cordilleras'));
