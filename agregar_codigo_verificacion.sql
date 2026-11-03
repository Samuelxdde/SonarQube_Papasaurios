-- =====================================================================
-- CÓDIGOS DE VERIFICACIÓN DE CORREO Y RECUPERACIÓN DE CONTRASEÑA
-- PAPASAURIOS
--
-- Crea la tabla que usan CodigoVerificacionDAO / EnviarCodigoRegistro /
-- VerificarCodigoRegistro (verificación del correo al registrarse) y
-- SolicitarRecuperacion / ConfirmarRecuperacion (recuperación de clave).
--
-- Ejecutar este script DESPUÉS de papasauriosDb.sql.
-- =====================================================================

CREATE TABLE IF NOT EXISTS `papasauriosdb`.`Codigo_verificacion` (
  `idCodigo` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `correo` VARCHAR(45) NOT NULL,
  `codigo` VARCHAR(6) NOT NULL,
  `tipo` ENUM('registro', 'recuperacion') NOT NULL,
  `creado` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `expira` DATETIME NOT NULL,
  `usado` BOOLEAN NOT NULL DEFAULT FALSE,
  `intentos` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`idCodigo`),
  INDEX `idx_codigo_verificacion_correo_tipo` (`correo`, `tipo`)
) ENGINE = InnoDB;
