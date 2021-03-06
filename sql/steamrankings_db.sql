-- MySQL Script generated by MySQL Workbench
-- 02/07/15 18:00:41
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema steamrankings_db
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `steamrankings_db` ;

-- -----------------------------------------------------
-- Schema steamrankings_db
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `steamrankings_db` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;
USE `steamrankings_db` ;

-- -----------------------------------------------------
-- Table `steamrankings_db`.`profiles`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `steamrankings_db`.`profiles` ;

CREATE TABLE IF NOT EXISTS `steamrankings_db`.`profiles` (
  `id` INT UNSIGNED NOT NULL,
  `community_id` LONGTEXT NOT NULL,
  `persona_name` LONGTEXT NULL,
  `real_name` LONGTEXT NULL,
  `location_country` LONGTEXT NULL,
  `location_province` LONGTEXT NULL,
  `location_city` LONGTEXT NULL,
  `avatar_full_url` LONGTEXT NULL,
  `avatar_medium_url` LONGTEXT NULL,
  `avatar_icon_url` LONGTEXT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `steamrankings_db`.`games`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `steamrankings_db`.`games` ;

CREATE TABLE IF NOT EXISTS `steamrankings_db`.`games` (
  `id` INT UNSIGNED NOT NULL,
  `name` LONGTEXT NOT NULL,
  `icon_url` LONGTEXT NULL,
  `logo_url` LONGTEXT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `steamrankings_db`.`achievements`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `steamrankings_db`.`achievements` ;

CREATE TABLE IF NOT EXISTS `steamrankings_db`.`achievements` (
  `id` INT NOT NULL,
  `games_id` INT UNSIGNED NOT NULL,
  `name` LONGTEXT NULL,
  `description` LONGTEXT NULL,
  `unlocked_icon_url` LONGTEXT NULL,
  `locked_icon_url` LONGTEXT NULL,
  PRIMARY KEY (`id`, `games_id`),
  INDEX `fk_achievements_games_idx` (`games_id` ASC),
  CONSTRAINT `fk_achievements_games`
    FOREIGN KEY (`games_id`)
    REFERENCES `steamrankings_db`.`games` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `steamrankings_db`.`profiles_has_games`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `steamrankings_db`.`profiles_has_games` ;

CREATE TABLE IF NOT EXISTS `steamrankings_db`.`profiles_has_games` (
  `profiles_id` INT UNSIGNED NOT NULL,
  `games_id` INT UNSIGNED NOT NULL,
  `total_play_time` INT ZEROFILL UNSIGNED NOT NULL,
  PRIMARY KEY (`profiles_id`, `games_id`),
  INDEX `fk_profliles_has_games_games1_idx` (`games_id` ASC),
  INDEX `fk_profliles_has_games_profliles1_idx` (`profiles_id` ASC),
  CONSTRAINT `fk_profliles_has_games_profliles1`
    FOREIGN KEY (`profiles_id`)
    REFERENCES `steamrankings_db`.`profiles` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_profliles_has_games_games1`
    FOREIGN KEY (`games_id`)
    REFERENCES `steamrankings_db`.`games` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `steamrankings_db`.`profiles_has_achievements`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `steamrankings_db`.`profiles_has_achievements` ;

CREATE TABLE IF NOT EXISTS `steamrankings_db`.`profiles_has_achievements` (
  `profiles_id` INT UNSIGNED NOT NULL,
  `achievements_id` INT NOT NULL,
  `achievements_games_id` INT UNSIGNED NOT NULL,
  `unlocked_timestamp` TIMESTAMP NULL,
  PRIMARY KEY (`profiles_id`, `achievements_id`, `achievements_games_id`),
  INDEX `fk_profliles_has_achievements_achievements1_idx` (`achievements_id` ASC, `achievements_games_id` ASC),
  INDEX `fk_profliles_has_achievements_profliles1_idx` (`profiles_id` ASC),
  CONSTRAINT `fk_profliles_has_achievements_profliles1`
    FOREIGN KEY (`profiles_id`)
    REFERENCES `steamrankings_db`.`profiles` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_profliles_has_achievements_achievements1`
    FOREIGN KEY (`achievements_id` , `achievements_games_id`)
    REFERENCES `steamrankings_db`.`achievements` (`id` , `games_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
