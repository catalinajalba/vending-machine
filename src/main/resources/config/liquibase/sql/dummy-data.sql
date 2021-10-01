-- LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES
(1,'admin','$2a$10$gSAhZrxMllrbgj/kkK9UceBPpChGWJA7SYIb1Mqo.n5aNLq1/oRrC','{\"5\": 0, \"10\": 0, \"20\": 0, \"50\": 0, \"100\": 0}','SELLER'),
(2,'user','$2a$10$VEjxo0jq2YG9Rbk2HmX9S.k1uZBGYUHdUcid3g/vfiEl7lwWgOH/K','{\"5\": 0, \"10\": 0, \"20\": 0, \"50\": 0, \"100\": 0}','BUYER'),
(3,'jhon','$2a$10$gSAhZrxMllrbgj/kkK9UceBPpChGWJA7SYIb1Mqo.n5aNLq1/oRrC','{\"5\": 0, \"10\": 0, \"20\": 0, \"50\": 0, \"100\": 0}','SELLER'),
(4,'mia','$2a$10$VEjxo0jq2YG9Rbk2HmX9S.k1uZBGYUHdUcid3g/vfiEl7lwWgOH/K','{\"5\": 0, \"10\": 0, \"20\": 0, \"50\": 0, \"100\": 0}','BUYER');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
-- UNLOCK TABLES;
