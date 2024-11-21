-- phpMyAdmin SQL Dump
-- version 5.1.1
-- https://www.phpmyadmin.net/
--
-- Hôte : localhost:8889
-- Généré le : jeu. 19 jan. 2023 à 16:19
-- Version du serveur : 5.7.34
-- Version de PHP : 7.4.21

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `express`
--

-- --------------------------------------------------------

DROP DATABASE IF EXISTS `express`;

-- --------------------------------------------------------

CREATE DATABASE IF NOT EXISTS `express`;

-- --------------------------------------------------------

--
-- Structure de la table `app_comment`
--

CREATE TABLE `express`.`app_comment` (
  `id` int(11) NOT NULL,
  `message` varchar(255) NOT NULL,
  `created_at` datetime NOT NULL,
  `app_note_id` int(11) NOT NULL,
  `app_user_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `app_note`
--

CREATE TABLE `express`.`app_note` (
  `id` int(11) NOT NULL,
  `note` varchar(255) NOT NULL,
  `likes` int(11) NOT NULL,
  `dislikes` int(11) NOT NULL,
  `created_at` datetime NOT NULL,
  `app_user_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `app_role`
--

CREATE TABLE `express`.`app_role` (
  `id` int(11) NOT NULL,
  `name` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `app_user`
--

CREATE TABLE `express`.`app_user` (
  `id` int(11) NOT NULL,
  `username` varchar(20) NOT NULL,
  `email` varchar(45) NOT NULL,
  `password` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `app_user_role`
--

CREATE TABLE `express`.`app_user_role` (
  `app_user_id` int(11) NOT NULL,
  `app_role_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Index pour la table `app_comment`
--
ALTER TABLE `express`.`app_comment`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_app_comment_app_note1_idx` (`app_note_id`),
  ADD KEY `fk_app_comment_app_user1_idx` (`app_user_id`);

--
-- Index pour la table `app_note`
--
ALTER TABLE `express`.`app_note`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_app_note_app_user1_idx` (`app_user_id`);

--
-- Index pour la table `app_role`
--
ALTER TABLE `express`.`app_role`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `app_user`
--
ALTER TABLE `express`.`app_user`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `app_user_role`
--
ALTER TABLE `express`.`app_user_role`
  ADD PRIMARY KEY (`app_user_id`,`app_role_id`),
  ADD KEY `fk_app_user_has_app_role_app_role1_idx` (`app_role_id`),
  ADD KEY `fk_app_user_has_app_role_app_user_idx` (`app_user_id`);

-- --------------------------------------------------------

--
-- AUTO_INCREMENT pour la table `app_comment`
--
ALTER TABLE `express`.`app_comment`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `app_note`
--
ALTER TABLE `express`.`app_note`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `app_role`
--
ALTER TABLE `express`.`app_role`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `app_user`
--
ALTER TABLE `express`.`app_user`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

-- --------------------------------------------------------

--
-- Contraintes pour la table `app_comment`
--
ALTER TABLE `express`.`app_comment`
  ADD CONSTRAINT `fk_app_comment_app_note1` FOREIGN KEY (`app_note_id`) REFERENCES `app_note` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_app_comment_app_user1` FOREIGN KEY (`app_user_id`) REFERENCES `app_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Contraintes pour la table `app_note`
--
ALTER TABLE `express`.`app_note`
  ADD CONSTRAINT `fk_app_note_app_user1` FOREIGN KEY (`app_user_id`) REFERENCES `app_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Contraintes pour la table `app_user_role`
--
ALTER TABLE `express`.`app_user_role`
  ADD CONSTRAINT `fk_app_user_has_app_role_app_role1` FOREIGN KEY (`app_role_id`) REFERENCES `app_role` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_app_user_has_app_role_app_user` FOREIGN KEY (`app_user_id`) REFERENCES `app_user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;


-- --------------------------------------------------------
-- INSERTS
-- --------------------------------------------------------

-- -----------------------------------------------------
-- Table `express`.`app_role`
-- -----------------------------------------------------
ALTER TABLE `express`.`app_role` AUTO_INCREMENT = 0;
INSERT INTO `express`.`app_role` (`name`) 
 VALUES
 ("ROLE_ADMIN"),
 ("ROLE_WRITER"),
 ("ROLE_READER");


-- -----------------------------------------------------
-- Table `express`.`app_user`
-- -----------------------------------------------------
ALTER TABLE `express`.`app_user` AUTO_INCREMENT = 0;
INSERT INTO `express`.`app_user` (`username`, `email`, `password`) 
 VALUES
 ("Dayïve", "davidhasselhoff@icloud.com", "$2a$10$P.kTsnbhFXAOOPtHpMbDgOYvCG1MQtV/QSy/TxXbWb8TnM4ytWL2u"),
 ("lebogo", "jcharlestribord@gmail.com", "$2a$10$P.kTsnbhFXAOOPtHpMbDgOYvCG1MQtV/QSy/TxXbWb8TnM4ytWL2u"),
 ("Jiminy Cricket", "jimcricket@hotmail.fr", "$2a$10$P.kTsnbhFXAOOPtHpMbDgOYvCG1MQtV/QSy/TxXbWb8TnM4ytWL2u"),
 ("Alex", "alexandremonet@yahoo.fr", "$2a$10$P.kTsnbhFXAOOPtHpMbDgOYvCG1MQtV/QSy/TxXbWb8TnM4ytWL2u"),
 ("the_killer", "thekiller88@hotmail.fr", "$2a$10$P.kTsnbhFXAOOPtHpMbDgOYvCG1MQtV/QSy/TxXbWb8TnM4ytWL2u"),
 ("Viv y Anne", "vbassoda@outlook.com", "$2a$10$P.kTsnbhFXAOOPtHpMbDgOYvCG1MQtV/QSy/TxXbWb8TnM4ytWL2u"),
 ("Cam", "camilla-taylor@icloud.com", "$2a$10$P.kTsnbhFXAOOPtHpMbDgOYvCG1MQtV/QSy/TxXbWb8TnM4ytWL2u"),
 ("Cortex91", "coco-cortex@hotmail.fr", "$2a$10$P.kTsnbhFXAOOPtHpMbDgOYvCG1MQtV/QSy/TxXbWb8TnM4ytWL2u"),
 ("famulan", "lamule@hotmail.fr", "$2a$10$P.kTsnbhFXAOOPtHpMbDgOYvCG1MQtV/QSy/TxXbWb8TnM4ytWL2u"),
 -- 10
 ("Petit-Jean", "jeanpetitdu77@icloud.com", "$2a$10$P.kTsnbhFXAOOPtHpMbDgOYvCG1MQtV/QSy/TxXbWb8TnM4ytWL2u"),
 ("Victor_MacBernik", "vicmcbern@outlook.com", "$2a$10$J7RODHLLAj2toM/zq0H2EOc2tMKW3NP5L1RBQPx6G4mGZ.ZEa8nEq"),
 ("Londubat", "neville-londubat@gmail.com", "$2a$10$J7RODHLLAj2toM/zq0H2EOc2tMKW3NP5L1RBQPx6G4mGZ.ZEa8nEq"),
 ("QuiVeutUneBastoss?", "celineroset@yahoo.com", "$2a$10$J7RODHLLAj2toM/zq0H2EOc2tMKW3NP5L1RBQPx6G4mGZ.ZEa8nEq"),
 ("Dami Nausorusse", "damiencharlet@wanadoo.fr", "$2a$10$J7RODHLLAj2toM/zq0H2EOc2tMKW3NP5L1RBQPx6G4mGZ.ZEa8nEq"),
 -- 15
 ("the-mountain-guide", "erik-kolstman@icloud.com", "$2a$10$J7RODHLLAj2toM/zq0H2EOc2tMKW3NP5L1RBQPx6G4mGZ.ZEa8nEq"),
 ("Nasser", "wearenasser@outlook.fr", "$2a$10$P.kTsnbhFXAOOPtHpMbDgOYvCG1MQtV/QSy/TxXbWb8TnM4ytWL2u");

-- -----------------------------------------------------
-- Table `express`.`app_user_role`
-- -----------------------------------------------------
INSERT INTO `express`.`app_user_role` (`app_user_id`, `app_role_id`) 
 VALUES
 (1, 1),
 (1, 2),
 (1, 3),
 (2, 2),
 (2, 3),
 (3, 2),
 (3, 3),
 (4, 2),
 (4, 3),
 (5, 2),
 (5, 3),
 (6, 2),
 (6, 3),
 (7, 2),
 (7, 3),
 (8, 2),
 (8, 3),
 (9, 2),
 (9, 3),
 (10, 2),
 (10, 3),
 (11, 2),
 (11, 3),
 (12, 2),
 (12, 3),
 (13, 2),
 (13, 3),
 (14, 2),
 (14, 3),
 (15, 2),
 (15, 3),
 (16, 2),
 (16, 3);


-- -----------------------------------------------------
-- Table `express`.`app_note`
-- -----------------------------------------------------
ALTER TABLE `express`.`app_note` AUTO_INCREMENT = 0;
INSERT INTO `express`.`app_note` (`note`, `likes`, `dislikes`, `created_at`, `app_user_id`) 
 VALUES
 ("Je vous souhaite la bienvenue à toutes et tous ! Amusez vous bien !", 326, 11, "2022-10-01 10:27:12", 1),
 ("Salut tout le monde ! J'espère que vous allez bien ! Bisous et à bientôt !", 10, 1, "2022-10-01 11:20:11", 4),
 ("Merci d'avoir ouvert ce tchat, c'est trop stylé !", 12, 0, "2022-10-01 11:21:46", 9),
 ("Qui est chaud pour une partie de Mario Kart ce soir ?", 0, 0, "2022-10-12 20:00:30", 2),
 -- 5
 ("Personne n'est chaud pour jouer à Mario ????", 0, 0, "2022-10-12 20:28:10", 2),
 ("A tous les provinciaux : Attention aux grèves SNCF prévues ce jeudi, prévoyez votre journée de télétravail...", 26, 2, "2022-10-14 08:02:12", 1),
 ("Oh dit là, dit oh dit là, dit quel beau jour vraiment !", 32, 0, "2022-10-14 10:05:00", 10),
 ("Try not to become a man of success. Rather become a man of value.", 40, 3, "2022-10-14 18:55:11", 5),
 ("C'est une bonne situation ça scribe ?", 155, 22, "2022-10-17 21:35:10", 7),
 -- 10
 ("Wanna know how I got these scars? My father was…a drinker. And a fiend. And one night he goes off crazier than usual. Mommy gets the kitchen knife to defend herself. He doesn’t like that. Not one bit.", 60, 4, "2022-10-18 22:45:00", 5),
 ("MIIIIAOUS OUI LA GUERRE !", 0, 0, "2022-10-20 08:44:55", 3),
 ("Disons que je ne suis pas vraiment monsieur tout-le-monde.", 0, 0, "2022-10-20 08:59:05", 3),
 ("Vous savez ce qui va pas monsieur Dusse ? C'est le planté du baton.", 10, 1, "2022-10-20 09:24:11", 5),
 ("You can fool all of the people some of the time, and some of the people all of the time, but you can't fool all of the people all of the time.", 88, 10, "2022-10-20 11:00:00", 7),
 -- 15
 ("Bientôt nous aurons tous à choisir entre le bien... et la facilité.", 8, 0, "2022-10-20 12:14:20", 9),
 ("Vous avez passé trois jours sur une plage à boire du Rhum ?", 7, 0, "2022-10-20 12:17:00", 2),
 ("L'autre fois il y avait du poulet, ça sentait trop bon... et le maitre cet abruti n'a pas voulu m'en donner un bout", 99, 0, "2022-10-20 12:18:10", 3),
 ("Faut qu'on reboot, faut qu'on reset...", 0, 0, "2022-10-22 17:01:10", 7),
 ("Donc... T'étonnes pas que je respecte R...", 199, 32, "2022-10-22 17:19:48", 5),
 -- 20
 ("Life is like riding a bicycle. To keep your balance, you must keep moving.", 62, 1, "2022-10-22 17:30:00", 8),
 ("Un air de voyez-vous avec ce dit John, voilà qu'elle sent le coup de foudre sur ce big love.", 76, 0, "2022-10-22 17:55:30", 4),
 ("À choisir ses rendez-vous, on dirait que j'truque un dé, on dirait que c'est harmonieux, on dirait que j'vais gagner", 55, 0, "2022-10-22 18:01:22", 6),
 ("Trouve-moi en pleine méditation dans un débit d’boissons, ma gueule de bois ferait passer Pinocchio pour un vrai p’tit garçon", 8, 0, "2022-10-22 18:27:00", 7),
 ("Remballe tes tongues maman on rentre à Melun !", 0, 0, "2022-10-22 18:29:00", 6),
 -- 25
 ("Une crème fouettée n'est pas une crème fouettée tant qu'elle n'est pas fouettée avec un fouet, tout le monde sait ça.", 103, 0, "2022-10-22 19:01:44", 10),
 ("Le plus gros poisson de la rivière ne le devient qu'en ne se faisant jamais attraper.", 15, 8, "2022-10-22 23:02:00", 10),
 ("En bonne et due forme Jack.", 0, 0, "2022-10-24 19:00:10", 2),
 ("Ça manque d'huiiiiile !", 0, 0, "2022-10-24 19:06:59", 3),
 ("Je ne suis pas monsieur Lebowski. C'est vous monsieur Lebowski. Moi je suis le Duc, c'est comme ça qu'il faut m'appeler. Ou alors... ça ou... j'sais pas, le grand Duc ou... l'Archiduc ou... Votre Altesse si vous êtes porté sur les titres.", 8, 1, "2022-10-25 08:45:15", 4),
 -- 30
 ("Tout a commencé là, quand mon avion a décollé... Oh la la c'est pas une histoire d'avion qui décolle... Ou plutôt si c'est une histoire de décollage... Je peux enfin commencer à tout vous raconter... tout a commencé là...", 13, 5, "2022-10-26 10:10:10", 6),
 ("Bonjour à tous ! Connaissez vous des ressources intéressantes pour se former à VueJS ? Merci d'avance pour vos réponses !", 114, 5, "2023-01-24 09:47:10", 11),
 ("Bonjour messieurs dames ! J'ai encore oublié le mot de passe... Est-ce que quelqu'un peut m'aider ?", 67, 0, "2023-02-03 11:59:30", 12),
 ("A tous les provinciaux : Attention aux grèves SNCF prévues ce jeudi, prévoyez votre journée de télétravail...", 27, 4, "2023-02-04 07:45:00", 1),
 ("Hello, j'ai un petit creux (oui déjà...). Je ne sais pas quoi manger ce midi donc si vous avez des idées, je suis preneur. J'irai faire 2-3 courses si je n'ai pas tous les ingrédients. À bientôt !", 25, 0, "2023-02-05 10:25:00", 13),
-- 35
 ("Michel Duduche qui est en tête, qui produit son effort, qui vient d'accélérer. 58 secondes d'avances ! T'es bien ! ALLER MICHEL ! ALLER ! Michel sur cette terrible étape on le savait, on l'avait dit, cette étape des pavés de Paris, elle est difficile !", 40, 0, "2023-02-23 21:06:00", 14),
 ("Qu'est-ce qui est jaune et qui attend ?", 40, 0, "2023-03-18 11:10:00", 12),
 ("Le mont Hood est un stratovolcan situé dans le nord de l’État américain de l'Oregon. On peut voir facilement la montagne depuis Portland et Vancouver. Il est le plus haut sommet de son état avec ses 3428m d'altitude. Vent fort au sommet.", 0, 0, "2023-03-18 11:21:00", 15),
 ("Darren Aronofsky : 'J'ai mis 10 ans pour faire The Whale.'", 0, 0, "2023-03-18 13:01:00", 11),
 ("Que l’arbitre soit en relation avec la VAR, il le faut mais après, on n’est pas au rugby...", 0, 0, "2023-03-18 13:35:00", 5),
 -- 40
 ("You are what you are, what you what you are, You are what you are, only what you are. You are what you are, what you what you are, You are what you are, only what you are. You are what you are, what you what you are.", 4, 0, "2023-03-18 13:52:00", 16),
 ("Super match des All Blacks hier soir ! Qui a regardé ?", 10, 1, "2023-10-21 15:32:10", 2);

-- -----------------------------------------------------
-- Table `express`.`app_comment`
-- -----------------------------------------------------
ALTER TABLE `express`.`app_comment` AUTO_INCREMENT = 0;
INSERT INTO `express`.`app_comment` (`message`, `created_at`, `app_note_id`, `app_user_id`) 
 VALUES
 ("Merci ! Hâte d'en apprendre d'avantage !", "2022-10-01 10:30:22", 1, 4),
 ("Merci beaucoup, à bientôt !", "2022-10-01 10:30:22", 1, 9),
 ("Merci ! Petite question au passage, on fait comment pour changer son mot de passe ?", "2022-10-01 10:33:23", 1, 3),
 ("Yeeeees ! Trop content d'être ici !!!", "2022-10-01 10:55:00", 1, 5),
 ("C'est super ! Merci !", "2022-10-01 11:22:21", 1, 9),
 ("Oui je vais bien et toi ?", "2022-10-01 11:20:11", 2, 9),
 ("Désolé, je ne peux pas ce soir", "2022-11-12 20:28:10", 5, 9),
 ("Merci pour l'info !", "2022-11-14 08:05:52", 6, 4),
 ("Encore ??? Mais ce n'est pas possible !", "2022-11-14 08:05:52", 6, 9),
 ("Ça marche, j'annule mon train...", "2022-11-14 08:06:11", 6, 7),
 ("Tu regardes un reportage sur Albert Einstein ?", "2022-10-14 19:05:00", 8, 10),
 ("Oui, j'adore !", "2022-10-14 19:09:11", 8, 5),
 ("Oh non ! Ne lance pas ça !!!!!!", "2022-10-17 21:45:33", 9, 8),
 ("Bon... ça y est je l'ai dans la tête...", "2022-10-17 21:45:45", 9, 8),
 ("Haha ! C'était le but !", "2022-10-17 21:49:45", 9, 7),
 ("Il passe à la télé ? Encore ?", "2022-10-17 21:58:50", 9, 4),
 ("Oui, mon maitre regarde.", "2022-10-17 21:59:10", 9, 3),
 ("Why so serious !!", "2022-10-18 22:45:00", 10, 5),
 ("Alors ? C'est bien les vacances au ski ?", "2022-10-20 09:50:00", 13, 9),
 ("La chance !!!", "2022-10-20 09:52:00", 13, 7),
 ("Oui c'est génial, il fait super beau en plus !", "2022-10-20 09:56:10", 13, 5),
 ("Profite bien !", "2022-10-20 09:59:44", 13, 7),
 ("Mon pauvre...", "2022-10-20 12:29:00", 17, 7),
 ("Quel petit chat malheureux !", "2022-10-20 13:19:05", 17, 1),
 ("Dommage pour lui, moi j'en donne un peu à Podric quand il est sage", "2022-10-20 13:19:05", 17, 11),
 ("Enorme, il perd pas le nord lui !", "2022-10-20 13:22:18", 17, 10),
 ("Tu écoutes Odezenne ? J'adore !", "2022-10-22 18:05:00", 22, 1),
 ("Tout à fait ! Vous connaissez ? J'aime beaucoup aussi !", "2022-10-23 08:01:59", 22, 6),
 ("Haha, j'en connais un qui regarde Camping !", "2022-10-23 08:09:00", 24, 10),
 ("Excellent ! Tu sors ça d'ou ?", "2022-10-22 20:10:00", 25, 4),
 ("Charlie et la Chocolaterie", "2022-10-22 20:12:50", 25, 10),
 ("Tu peux commencer par consulter la documentation officielle. Il y a surement des ressources intéressantes sur YouTube. Tu peux aussi regarder sur OC pour commencer c'est bien. Bon courage à toi ! Tu vas voir, c'est pas mal Vue !", "2023-01-25 08:02:50", 31, 1),
 ("J'ai un cours Udemy que je peux te passer si tu veux.", "2023-01-25 10:48:50", 31, 2),
 ("Bon courage à toi :)", "2023-01-25 10:48:50", 31, 9),
 ("Merci pour vos réponses ! Lebogo, je veux bien du coup, si c'est toujours ok !", "2023-01-25 17:41:50", 31, 12),
 ("Pas de soucis, je t'envoie ça !", "2023-01-25 17:42:59", 31, 2),
 ("Encore ??? :o", "2023-02-03 12:10:30", 32, 11),
 ("Je te l'envoie par sms !", "2023-02-03 17:45:00", 32, 13),
 ("NOOOOOONNNN ! J'avais un rdv dans le 17ème !!!", "2023-02-04 07:50:00", 33, 9),
 ("CA marche, j'annule mon train !", "2023-02-04 08:02:00", 33, 7),
 ("Merci pour l'info !", "2023-02-04 08:03:10", 33, 11),
 ("Hello ! Est-ce que vous savez si la ratp est en grève aussi ?", "2023-02-04 08:05:00", 33, 2),
 ("Oui, 50% des transports seront assurés, ne vous embêtez pas, restez chez vous, on se voit demain !", "2023-02-04 08:15:00", 33, 1),
 ("Ok ça marche ! A tout de suite sur Teams !", "2023-02-04 08:11:10", 33, 13),
 ("Moi aussi j'ai faiiiim !", "2023-02-05 10:41:00", 34, 2),
 ("Pour ma part, ce midi c'est haricots verts et cordons bleus !", "2023-02-05 10:42:00", 34, 2),
 ("On va chercher une pizza avec the_killer si tu veux.", "2023-02-05 10:42:00", 34, 11),
 ("Des frissons cette étape du Tour de France ! J'adore le commentateur !", "2023-02-23 21:13:00", 35, 13),
 ("...Et Michel qui est en train de prendre le maillot Jaune ! Je me retourne, l'écart est important ! 1:30 ! 1:30 d'avance ! Allez Michel ! T'es bon !", "2023-02-23 21:15:00", 35, 4),
 ("Jaune à temps ?", "2023-02-05 11:21:00", 36, 15),
 ("lol", "2023-02-05 11:22:00", 36, 4),
 ("Non c'est Jonathan !", "2023-02-05 11:23:00", 36, 12),
 ("A l'instar des Alpes du Nord il y a eu pas mal de chute de neige tardive dans le Pacific Northwest aussi.", "2023-02-05 11:27:00", 37, 1),
 ("Tout à fait. De la neige à 5 minutes du parking de Timberline, à moins de 2000m d'altitude, fin aôut !", "2023-02-05 11:29:00", 37, 15),
 ("Vu en concert à Nancy en 2013, vous avez mis le feu !!", "2023-02-05 14:21:00", 40, 4),
 ("Magnifique ! Ils vont allez au bout !", "2023-10-21 16:00:00", 41, 4),
 ("Les futurs champions du monde !", "2023-10-21 21:52:00", 41, 11);