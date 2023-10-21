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

-- --------------------------------------------------------

--
-- BASIC INSERTS
-- 

-- -----------------------------------------------------
-- Table `express`.`app_role`
-- -----------------------------------------------------
ALTER TABLE `express`.`app_role` AUTO_INCREMENT = 0;
INSERT INTO `express`.`app_role` (`name`) 
 VALUES
 ("ROLE_ADMIN"),
 ("ROLE_WRITER"),
 ("ROLE_READER");