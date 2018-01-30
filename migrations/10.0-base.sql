-- Initial state of database schema

CREATE TABLE `em` (
  `name` varchar(40) NOT NULL,
  `w_id` int(11) NOT NULL,
  `role` varchar(20) DEFAULT NULL,
  `ext` char(5) NOT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `gcp` (
  `GCP_CD` varchar(13) NOT NULL,
  `GLN_NM` varchar(255) NOT NULL,
  `GLN_ADDR_02` varchar(38) NOT NULL,
  `GLN_ADDR_03` varchar(38) NOT NULL,
  `GLN_ADDR_04` varchar(38) NOT NULL,
  `GLN_ADDR_POSTALCODE` varchar(38) NOT NULL,
  `GLN_ADDR_CITY` varchar(38) NOT NULL,
  `CONTACT_TEL` varchar(255) NOT NULL,
  `CONTACT_MAIL` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `gtin` (
  `gtin_cd` varchar(13) NOT NULL,
  `gcp_cd` varchar(13) DEFAULT NULL,
  `gtin_nm` varchar(255) DEFAULT NULL,
  `m_g` float DEFAULT NULL COMMENT 'gramme',
  `l_th` int(11) DEFAULT NULL,
  `p_id` int(11) NOT NULL AUTO_INCREMENT,
  `ds` int(11) NOT NULL,
  `min_qt` int(11) NOT NULL,
  PRIMARY KEY (`p_id`)
) ENGINE=InnoDB AUTO_INCREMENT=146921 DEFAULT CHARSET=utf8;

CREATE TABLE `stock` (
  `p_id` int(11) NOT NULL,
  `w_id` int(11) NOT NULL,
  `hld` int(11) NOT NULL,
  PRIMARY KEY (`p_id`,`w_id`),
  KEY `p_id` (`p_id`),
  CONSTRAINT `stock_ibfk_1` FOREIGN KEY (`p_id`) REFERENCES `gtin` (`p_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
