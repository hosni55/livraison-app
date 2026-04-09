-- ============================================================
-- ORACLE DATABASE SCHEMA — LivraisonCom Supervision System
-- Database: BDG_LivraisonCom_25
-- ============================================================

-- ============================================================
-- 1. ORIGINAL TABLES
-- ============================================================

-- Table: Postes
CREATE TABLE Postes (
    codeposte NUMBER PRIMARY KEY,
    libelle VARCHAR2(100) NOT NULL,
    indice NUMBER
);

-- Table: Personnel
CREATE TABLE Personnel (
    idpers NUMBER PRIMARY KEY,
    nompers VARCHAR2(50) NOT NULL,
    prenompers VARCHAR2(50) NOT NULL,
    adrpers VARCHAR2(200),
    villepers VARCHAR2(50),
    telpers VARCHAR2(20),
    d_embauche DATE,
    Login VARCHAR2(50) UNIQUE NOT NULL,
    motP VARCHAR2(100) NOT NULL,
    codeposte NUMBER,
    CONSTRAINT fk_personnel_poste FOREIGN KEY (codeposte) REFERENCES Postes(codeposte)
);

-- Table: Clients
CREATE TABLE Clients (
    noclt NUMBER PRIMARY KEY,
    nomclt VARCHAR2(50) NOT NULL,
    prenomclt VARCHAR2(50),
    adrclt VARCHAR2(200) NOT NULL,
    villeclt VARCHAR2(50),
    code_postal VARCHAR2(10),
    telclt VARCHAR2(20),
    adrmail VARCHAR2(100)
);

-- Table: Articles
CREATE TABLE Articles (
    refart VARCHAR2(20) PRIMARY KEY,
    designation VARCHAR2(100) NOT NULL,
    prixA NUMBER(10,2),
    prixV NUMBER(10,2),
    codetva NUMBER(5,2),
    categorie VARCHAR2(50),
    qtestk NUMBER
);

-- Table: Commandes
CREATE TABLE Commandes (
    nocde NUMBER PRIMARY KEY,
    noclt NUMBER NOT NULL,
    datecde DATE NOT NULL,
    etatcde VARCHAR2(20) DEFAULT 'EN_ATTENTE',
    CONSTRAINT fk_commande_client FOREIGN KEY (noclt) REFERENCES Clients(noclt)
);

-- Table: LigCdes (Lignes Commandes)
CREATE TABLE LigCdes (
    nocde NUMBER NOT NULL,
    refart VARCHAR2(20) NOT NULL,
    qtecde NUMBER NOT NULL,
    CONSTRAINT pk_ligcdes PRIMARY KEY (nocde, refart),
    CONSTRAINT fk_ligcdes_commande FOREIGN KEY (nocde) REFERENCES Commandes(nocde),
    CONSTRAINT fk_ligcdes_article FOREIGN KEY (refart) REFERENCES Articles(refart)
);

-- Table: LivraisonCom
CREATE TABLE LivraisonCom (
    nocde NUMBER PRIMARY KEY,
    dateliv DATE,
    livreur NUMBER,
    modepay VARCHAR2(30),
    etatliv VARCHAR2(30) DEFAULT 'PLANIFIEE',
    CONSTRAINT fk_livraison_commande FOREIGN KEY (nocde) REFERENCES Commandes(nocde),
    CONSTRAINT fk_livraison_livreur FOREIGN KEY (livreur) REFERENCES Personnel(idpers)
);

-- ============================================================
-- 2. NEW TABLES
-- ============================================================

-- Table: NOTIFICATIONS
CREATE TABLE NOTIFICATIONS (
    id NUMBER PRIMARY KEY,
    sender_id NUMBER NOT NULL,
    receiver_id NUMBER NOT NULL,
    message VARCHAR2(500) NOT NULL,
    type VARCHAR2(30) NOT NULL,
    nocde NUMBER,
    is_read NUMBER(1) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notif_sender FOREIGN KEY (sender_id) REFERENCES Personnel(idpers),
    CONSTRAINT fk_notif_receiver FOREIGN KEY (receiver_id) REFERENCES Personnel(idpers),
    CONSTRAINT fk_notif_commande FOREIGN KEY (nocde) REFERENCES Commandes(nocde)
);

-- Table: GPS_POSITIONS
CREATE TABLE GPS_POSITIONS (
    id NUMBER PRIMARY KEY,
    livreur_id NUMBER NOT NULL,
    latitude NUMBER(10, 8) NOT NULL,
    longitude NUMBER(11, 8) NOT NULL,
    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_gps_livreur FOREIGN KEY (livreur_id) REFERENCES Personnel(idpers)
);

-- Table: DELIVERY_PROOFS
CREATE TABLE DELIVERY_PROOFS (
    id NUMBER PRIMARY KEY,
    nocde NUMBER NOT NULL,
    photo_path VARCHAR2(500),
    signature_path VARCHAR2(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_proof_commande FOREIGN KEY (nocde) REFERENCES Commandes(nocde)
);

-- Table: MESSAGES
CREATE TABLE MESSAGES (
    id NUMBER PRIMARY KEY,
    sender_id NUMBER NOT NULL,
    receiver_id NUMBER NOT NULL,
    content VARCHAR2(1000) NOT NULL,
    nocde NUMBER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_msg_sender FOREIGN KEY (sender_id) REFERENCES Personnel(idpers),
    CONSTRAINT fk_msg_receiver FOREIGN KEY (receiver_id) REFERENCES Personnel(idpers),
    CONSTRAINT fk_msg_commande FOREIGN KEY (nocde) REFERENCES Commandes(nocde)
);

-- ============================================================
-- 3. SEQUENCES
-- ============================================================

CREATE SEQUENCE SEQ_PERSONNEL START WITH 100 INCREMENT BY 1;
CREATE SEQUENCE SEQ_CLIENTS START WITH 100 INCREMENT BY 1;
CREATE SEQUENCE SEQ_COMMANDES START WITH 100 INCREMENT BY 1;
CREATE SEQUENCE SEQ_NOTIFICATIONS START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE SEQ_GPS_POSITIONS START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE SEQ_DELIVERY_PROOFS START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE SEQ_MESSAGES START WITH 1 INCREMENT BY 1;

-- ============================================================
-- 4. TRIGGERS FOR AUTOMATIC TIMESTAMPS
-- ============================================================

-- Trigger: NOTIFICATIONS created_at
CREATE OR REPLACE TRIGGER TRG_NOTIF_CREATED
BEFORE INSERT ON NOTIFICATIONS
FOR EACH ROW
BEGIN
    :NEW.created_at := CURRENT_TIMESTAMP;
END;
/

-- Trigger: GPS_POSITIONS recorded_at
CREATE OR REPLACE TRIGGER TRG_GPS_RECORDED
BEFORE INSERT ON GPS_POSITIONS
FOR EACH ROW
BEGIN
    :NEW.recorded_at := CURRENT_TIMESTAMP;
END;
/

-- Trigger: DELIVERY_PROOFS created_at
CREATE OR REPLACE TRIGGER TRG_PROOF_CREATED
BEFORE INSERT ON DELIVERY_PROOFS
FOR EACH ROW
BEGIN
    :NEW.created_at := CURRENT_TIMESTAMP;
END;
/

-- Trigger: MESSAGES created_at
CREATE OR REPLACE TRIGGER TRG_MSG_CREATED
BEFORE INSERT ON MESSAGES
FOR EACH ROW
BEGIN
    :NEW.created_at := CURRENT_TIMESTAMP;
END;
/

-- ============================================================
-- 5. INDEXES FOR PERFORMANCE
-- ============================================================

CREATE INDEX IDX_LIVRAISON_ETAT ON LivraisonCom(etatliv);
CREATE INDEX IDX_LIVRAISON_DATE ON LivraisonCom(dateliv);
CREATE INDEX IDX_LIVRAISON_LIVREUR ON LivraisonCom(livreur);
CREATE INDEX IDX_COMMANDE_CLIENT ON Commandes(noclt);
CREATE INDEX IDX_COMMANDE_DATE ON Commandes(datecde);
CREATE INDEX IDX_NOTIF_RECEIVER ON NOTIFICATIONS(receiver_id);
CREATE INDEX IDX_NOTIF_READ ON NOTIFICATIONS(is_read);
CREATE INDEX IDX_GPS_LIVREUR ON GPS_POSITIONS(livreur_id);
CREATE INDEX IDX_GPS_RECORDED ON GPS_POSITIONS(recorded_at);
CREATE INDEX IDX_MSG_RECEIVER ON MESSAGES(receiver_id);
CREATE INDEX IDX_MSG_CREATED ON MESSAGES(created_at);
CREATE INDEX IDX_PROOF_COMMANDE ON DELIVERY_PROOFS(nocde);

-- ============================================================
-- 6. SAMPLE DATA
-- ============================================================

-- 6.1 Postes
INSERT INTO Postes (codeposte, libelle, indice) VALUES (1, 'Livreur', 1);
INSERT INTO Postes (codeposte, libelle, indice) VALUES (2, 'Controleur', 2);
INSERT INTO Postes (codeposte, libelle, indice) VALUES (3, 'Chef d equipe', 3);
INSERT INTO Postes (codeposte, libelle, indice) VALUES (4, 'Responsable logistique', 4);

-- 6.2 Personnel (5 livreurs + 2 controleurs)
-- Livreurs (codeposte = 1)
INSERT INTO Personnel (idpers, nompers, prenompers, adrpers, villepers, telpers, d_embauche, Login, motP, codeposte)
VALUES (1, 'Benali', 'Karim', '12 Rue des Lilas', 'Tunis', '22111222', TO_DATE('2022-03-15', 'YYYY-MM-DD'), 'karim.benali', 'livreur123', 1);

INSERT INTO Personnel (idpers, nompers, prenompers, adrpers, villepers, telpers, d_embauche, Login, motP, codeposte)
VALUES (2, 'Mansour', 'Amine', '45 Ave Habib Bourguiba', 'Sfax', '24333444', TO_DATE('2021-07-20', 'YYYY-MM-DD'), 'amine.mansour', 'livreur123', 1);

INSERT INTO Personnel (idpers, nompers, prenompers, adrpers, villepers, telpers, d_embauche, Login, motP, codeposte)
VALUES (3, 'Trabelsi', 'Youssef', '8 Rue de la Paix', 'Sousse', '23555666', TO_DATE('2023-01-10', 'YYYY-MM-DD'), 'youssef.trabelsi', 'livreur123', 1);

INSERT INTO Personnel (idpers, nompers, prenompers, adrpers, villepers, telpers, d_embauche, Login, motP, codeposte)
VALUES (4, 'Hamdi', 'Mohamed', '22 Blvd du 7 Novembre', 'Ariana', '21777888', TO_DATE('2020-11-05', 'YYYY-MM-DD'), 'mohamed.hamdi', 'livreur123', 1);

INSERT INTO Personnel (idpers, nompers, prenompers, adrpers, villepers, telpers, d_embauche, Login, motP, codeposte)
VALUES (5, 'Jebali', 'Sami', '33 Rue El Menzah', 'Tunis', '22999000', TO_DATE('2022-09-01', 'YYYY-MM-DD'), 'sami.jebali', 'livreur123', 1);

-- Controleurs (codeposte = 2)
INSERT INTO Personnel (idpers, nompers, prenompers, adrpers, villepers, telpers, d_embauche, Login, motP, codeposte)
VALUES (6, 'Bouazizi', 'Fatma', '15 Ave de la Liberte', 'Tunis', '22123456', TO_DATE('2019-05-12', 'YYYY-MM-DD'), 'fatma.bouazizi', 'controleur123', 2);

INSERT INTO Personnel (idpers, nompers, prenompers, adrpers, villepers, telpers, d_embauche, Login, motP, codeposte)
VALUES (7, 'Chaabane', 'Nadia', '7 Rue des Jasmins', 'Sousse', '23654321', TO_DATE('2020-02-28', 'YYYY-MM-DD'), 'nadia.chaabane', 'controleur123', 2);

-- 6.3 Clients (15 clients)
INSERT INTO Clients (noclt, nomclt, prenomclt, adrclt, villeclt, code_postal, telclt, adrmail)
VALUES (1, 'Gharbi', 'Ahmed', '10 Rue de Marseille', 'Tunis', '1000', '98111222', 'ahmed.gharbi@email.com');

INSERT INTO Clients (noclt, nomclt, prenomclt, adrclt, villeclt, code_postal, telclt, adrmail)
VALUES (2, 'Saidi', 'Leila', '25 Ave Farhat Hached', 'Sfax', '3000', '98333444', 'leila.saidi@email.com');

INSERT INTO Clients (noclt, nomclt, prenomclt, adrclt, villeclt, code_postal, telclt, adrmail)
VALUES (3, 'Khalfallah', 'Rami', '5 Rue Ibn Khaldoun', 'Sousse', '4000', '98555666', 'rami.khalfallah@email.com');

INSERT INTO Clients (noclt, nomclt, prenomclt, adrclt, villeclt, code_postal, telclt, adrmail)
VALUES (4, 'Mejri', 'Salma', '18 Blvd Ali Belhouane', 'Ariana', '2080', '98777888', 'salma.mejri@email.com');

INSERT INTO Clients (noclt, nomclt, prenomclt, adrclt, villeclt, code_postal, telclt, adrmail)
VALUES (5, 'Dridi', 'Omar', '42 Rue de Palestine', 'Tunis', '1002', '98999000', 'omar.dridi@email.com');

INSERT INTO Clients (noclt, nomclt, prenomclt, adrclt, villeclt, code_postal, telclt, adrmail)
VALUES (6, 'Zaier', 'Ines', '9 Ave Bourguiba', 'Nabeul', '8000', '99111222', 'ines.zaier@email.com');

INSERT INTO Clients (noclt, nomclt, prenomclt, adrclt, villeclt, code_postal, telclt, adrmail)
VALUES (7, 'Baccar', 'Hichem', '30 Rue de la Republique', 'Bizerte', '7000', '99333444', 'hichem.baccar@email.com');

INSERT INTO Clients (noclt, nomclt, prenomclt, adrclt, villeclt, code_postal, telclt, adrmail)
VALUES (8, 'Louati', 'Amira', '14 Rue El Jazira', 'Tunis', '1001', '99555666', 'amira.louati@email.com');

INSERT INTO Clients (noclt, nomclt, prenomclt, adrclt, villeclt, code_postal, telclt, adrmail)
VALUES (9, 'Fakhfakh', 'Tarek', '27 Ave de Carthage', 'La Marsa', '2078', '99777888', 'tarek.fakhfakh@email.com');

INSERT INTO Clients (noclt, nomclt, prenomclt, adrclt, villeclt, code_postal, telclt, adrmail)
VALUES (10, 'Sfar', 'Rim', '3 Rue des Orangers', 'Sousse', '4000', '99999000', 'rim.sfar@email.com');

INSERT INTO Clients (noclt, nomclt, prenomclt, adrclt, villeclt, code_postal, telclt, adrmail)
VALUES (11, 'Karray', 'Walid', '50 Rue de Gabes', 'Sfax', '3000', '97111222', 'walid.karray@email.com');

INSERT INTO Clients (noclt, nomclt, prenomclt, adrclt, villeclt, code_postal, telclt, adrmail)
VALUES (12, 'Haddad', 'Mounir', '11 Ave de l Independance', 'Kairouan', '3100', '97333444', 'mounir.haddad@email.com');

INSERT INTO Clients (noclt, nomclt, prenomclt, adrclt, villeclt, code_postal, telclt, adrmail)
VALUES (13, 'Cheikh', 'Sonia', '6 Rue de Monastir', 'Monastir', '5000', '97555666', 'sonia.cheikh@email.com');

INSERT INTO Clients (noclt, nomclt, prenomclt, adrclt, villeclt, code_postal, telclt, adrmail)
VALUES (14, 'Mzali', 'Karim', '19 Blvd du 14 Janvier', 'Tunis', '1000', '97777888', 'karim.mzali@email.com');

INSERT INTO Clients (noclt, nomclt, prenomclt, adrclt, villeclt, code_postal, telclt, adrmail)
VALUES (15, 'Ben Amor', 'Hela', '38 Rue de Sidi Bouzid', 'Sidi Bouzid', '9100', '97999000', 'hela.benamour@email.com');

-- 6.4 Articles (20 articles)
INSERT INTO Articles (refart, designation, prixA, prixV, codetva, categorie, qtestk)
VALUES ('ART001', 'Ordinateur Portable HP 15', 1200.00, 1450.00, 19.00, 'Informatique', 50);

INSERT INTO Articles (refart, designation, prixA, prixV, codetva, categorie, qtestk)
VALUES ('ART002', 'Imprimante Canon PIXMA', 180.00, 220.00, 19.00, 'Informatique', 30);

INSERT INTO Articles (refart, designation, prixA, prixV, codetva, categorie, qtestk)
VALUES ('ART003', 'Ecran Samsung 24 pouces', 350.00, 420.00, 19.00, 'Informatique', 25);

INSERT INTO Articles (refart, designation, prixA, prixV, codetva, categorie, qtestk)
VALUES ('ART004', 'Clavier Logitech K380', 45.00, 55.00, 19.00, 'Accessoires', 100);

INSERT INTO Articles (refart, designation, prixA, prixV, codetva, categorie, qtestk)
VALUES ('ART005', 'Souris sans fil Microsoft', 30.00, 38.00, 19.00, 'Accessoires', 120);

INSERT INTO Articles (refart, designation, prixA, prixV, codetva, categorie, qtestk)
VALUES ('ART006', 'Disque Dur Externe 1TB', 85.00, 105.00, 19.00, 'Stockage', 60);

INSERT INTO Articles (refart, designation, prixA, prixV, codetva, categorie, qtestk)
VALUES ('ART007', 'Cable HDMI 2m', 8.00, 12.00, 19.00, 'Accessoires', 200);

INSERT INTO Articles (refart, designation, prixA, prixV, codetva, categorie, qtestk)
VALUES ('ART008', 'Webcam Logitech C920', 95.00, 115.00, 19.00, 'Accessoires', 40);

INSERT INTO Articles (refart, designation, prixA, prixV, codetva, categorie, qtestk)
VALUES ('ART009', 'Casque Audio JBL', 60.00, 75.00, 19.00, 'Audio', 80);

INSERT INTO Articles (refart, designation, prixA, prixV, codetva, categorie, qtestk)
VALUES ('ART010', 'Enceinte Bluetooth Sony', 120.00, 150.00, 19.00, 'Audio', 35);

INSERT INTO Articles (refart, designation, prixA, prixV, codetva, categorie, qtestk)
VALUES ('ART011', 'Tablette Samsung Galaxy Tab', 400.00, 480.00, 19.00, 'Informatique', 20);

INSERT INTO Articles (refart, designation, prixA, prixV, codetva, categorie, qtestk)
VALUES ('ART012', 'Chargeur USB-C Rapide', 15.00, 22.00, 19.00, 'Accessoires', 150);

INSERT INTO Articles (refart, designation, prixA, prixV, codetva, categorie, qtestk)
VALUES ('ART013', 'Hub USB 7 ports', 25.00, 35.00, 19.00, 'Accessoires', 90);

INSERT INTO Articles (refart, designation, prixA, prixV, codetva, categorie, qtestk)
VALUES ('ART014', 'Cable Ethernet Cat6 5m', 10.00, 15.00, 19.00, 'Reseau', 180);

INSERT INTO Articles (refart, designation, prixA, prixV, codetva, categorie, qtestk)
VALUES ('ART015', 'Switch Reseau 8 ports', 40.00, 52.00, 19.00, 'Reseau', 45);

INSERT INTO Articles (refart, designation, prixA, prixV, codetva, categorie, qtestk)
VALUES ('ART016', 'Routeur WiFi TP-Link', 55.00, 70.00, 19.00, 'Reseau', 55);

INSERT INTO Articles (refart, designation, prixA, prixV, codetva, categorie, qtestk)
VALUES ('ART017', 'Ondeur APC 650VA', 150.00, 185.00, 19.00, 'Energie', 25);

INSERT INTO Articles (refart, designation, prixA, prixV, codetva, categorie, qtestk)
VALUES ('ART018', 'Multiprise Parafoudre 6 prises', 18.00, 25.00, 19.00, 'Energie', 100);

INSERT INTO Articles (refart, designation, prixA, prixV, codetva, categorie, qtestk)
VALUES ('ART019', 'Sac a dos ordinateur', 35.00, 45.00, 19.00, 'Accessoires', 70);

INSERT INTO Articles (refart, designation, prixA, prixV, codetva, categorie, qtestk)
VALUES ('ART020', 'Tapis de souris XL', 12.00, 18.00, 19.00, 'Accessoires', 110);

-- 6.5 Commandes (20 commandes)
INSERT INTO Commandes (nocde, noclt, datecde, etatcde)
VALUES (1, 1, TO_DATE('2025-03-01 09:30:00', 'YYYY-MM-DD HH24:MI:SS'), 'VALIDEE');

INSERT INTO Commandes (nocde, noclt, datecde, etatcde)
VALUES (2, 2, TO_DATE('2025-03-01 10:15:00', 'YYYY-MM-DD HH24:MI:SS'), 'VALIDEE');

INSERT INTO Commandes (nocde, noclt, datecde, etatcde)
VALUES (3, 3, TO_DATE('2025-03-02 08:45:00', 'YYYY-MM-DD HH24:MI:SS'), 'VALIDEE');

INSERT INTO Commandes (nocde, noclt, datecde, etatcde)
VALUES (4, 4, TO_DATE('2025-03-02 11:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'VALIDEE');

INSERT INTO Commandes (nocde, noclt, datecde, etatcde)
VALUES (5, 5, TO_DATE('2025-03-03 09:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'VALIDEE');

INSERT INTO Commandes (nocde, noclt, datecde, etatcde)
VALUES (6, 6, TO_DATE('2025-03-03 14:30:00', 'YYYY-MM-DD HH24:MI:SS'), 'VALIDEE');

INSERT INTO Commandes (nocde, noclt, datecde, etatcde)
VALUES (7, 7, TO_DATE('2025-03-04 10:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'VALIDEE');

INSERT INTO Commandes (nocde, noclt, datecde, etatcde)
VALUES (8, 8, TO_DATE('2025-03-04 15:20:00', 'YYYY-MM-DD HH24:MI:SS'), 'VALIDEE');

INSERT INTO Commandes (nocde, noclt, datecde, etatcde)
VALUES (9, 9, TO_DATE('2025-03-05 08:30:00', 'YYYY-MM-DD HH24:MI:SS'), 'VALIDEE');

INSERT INTO Commandes (nocde, noclt, datecde, etatcde)
VALUES (10, 10, TO_DATE('2025-03-05 13:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'VALIDEE');

INSERT INTO Commandes (nocde, noclt, datecde, etatcde)
VALUES (11, 11, TO_DATE('2025-03-06 09:45:00', 'YYYY-MM-DD HH24:MI:SS'), 'VALIDEE');

INSERT INTO Commandes (nocde, noclt, datecde, etatcde)
VALUES (12, 12, TO_DATE('2025-03-06 11:30:00', 'YYYY-MM-DD HH24:MI:SS'), 'VALIDEE');

INSERT INTO Commandes (nocde, noclt, datecde, etatcde)
VALUES (13, 13, TO_DATE('2025-03-07 10:15:00', 'YYYY-MM-DD HH24:MI:SS'), 'VALIDEE');

INSERT INTO Commandes (nocde, noclt, datecde, etatcde)
VALUES (14, 14, TO_DATE('2025-03-07 14:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'VALIDEE');

INSERT INTO Commandes (nocde, noclt, datecde, etatcde)
VALUES (15, 15, TO_DATE('2025-03-08 09:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'VALIDEE');

INSERT INTO Commandes (nocde, noclt, datecde, etatcde)
VALUES (16, 1, TO_DATE('2025-03-08 11:45:00', 'YYYY-MM-DD HH24:MI:SS'), 'VALIDEE');

INSERT INTO Commandes (nocde, noclt, datecde, etatcde)
VALUES (17, 3, TO_DATE('2025-03-09 08:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'VALIDEE');

INSERT INTO Commandes (nocde, noclt, datecde, etatcde)
VALUES (18, 5, TO_DATE('2025-03-09 10:30:00', 'YYYY-MM-DD HH24:MI:SS'), 'VALIDEE');

INSERT INTO Commandes (nocde, noclt, datecde, etatcde)
VALUES (19, 8, TO_DATE('2025-03-10 09:15:00', 'YYYY-MM-DD HH24:MI:SS'), 'VALIDEE');

INSERT INTO Commandes (nocde, noclt, datecde, etatcde)
VALUES (20, 10, TO_DATE('2025-03-10 14:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'VALIDEE');

-- 6.6 LigCdes (Lignes de commandes)
INSERT INTO LigCdes (nocde, refart, qtecde) VALUES (1, 'ART001', 1);
INSERT INTO LigCdes (nocde, refart, qtecde) VALUES (1, 'ART004', 2);
INSERT INTO LigCdes (nocde, refart, qtecde) VALUES (2, 'ART002', 1);
INSERT INTO LigCdes (nocde, refart, qtecde) VALUES (2, 'ART007', 3);
INSERT INTO LigCdes (nocde, refart, qtecde) VALUES (3, 'ART003', 1);
INSERT INTO LigCdes (nocde, refart, qtecde) VALUES (3, 'ART005', 1);
INSERT INTO LigCdes (nocde, refart, qtecde) VALUES (4, 'ART006', 2);
INSERT INTO LigCdes (nocde, refart, qtecde) VALUES (4, 'ART012', 4);
INSERT INTO LigCdes (nocde, refart, qtecde) VALUES (5, 'ART009', 1);
INSERT INTO LigCdes (nocde, refart, qtecde) VALUES (5, 'ART010', 1);
INSERT INTO LigCdes (nocde, refart, qtecde) VALUES (6, 'ART011', 1);
INSERT INTO LigCdes (nocde, refart, qtecde) VALUES (6, 'ART019', 1);
INSERT INTO LigCdes (nocde, refart, qtecde) VALUES (7, 'ART014', 5);
INSERT INTO LigCdes (nocde, refart, qtecde) VALUES (7, 'ART015', 2);
INSERT INTO LigCdes (nocde, refart, qtecde) VALUES (8, 'ART016', 1);
INSERT INTO LigCdes (nocde, refart, qtecde) VALUES (8, 'ART018', 3);
INSERT INTO LigCdes (nocde, refart, qtecde) VALUES (9, 'ART001', 1);
INSERT INTO LigCdes (nocde, refart, qtecde) VALUES (9, 'ART008', 1);
INSERT INTO LigCdes (nocde, refart, qtecde) VALUES (10, 'ART020', 2);
INSERT INTO LigCdes (nocde, refart, qtecde) VALUES (10, 'ART013', 1);
INSERT INTO LigCdes (nocde, refart, qtecde) VALUES (11, 'ART017', 1);
INSERT INTO LigCdes (nocde, refart, qtecde) VALUES (11, 'ART007', 2);
INSERT INTO LigCdes (nocde, refart, qtecde) VALUES (12, 'ART004', 3);
INSERT INTO LigCdes (nocde, refart, qtecde) VALUES (12, 'ART005', 2);
INSERT INTO LigCdes (nocde, refart, qtecde) VALUES (13, 'ART002', 1);
INSERT INTO LigCdes (nocde, refart, qtecde) VALUES (13, 'ART006', 1);
INSERT INTO LigCdes (nocde, refart, qtecde) VALUES (14, 'ART003', 2);
INSERT INTO LigCdes (nocde, refart, qtecde) VALUES (14, 'ART012', 2);
INSERT INTO LigCdes (nocde, refart, qtecde) VALUES (15, 'ART009', 2);
INSERT INTO LigCdes (nocde, refart, qtecde) VALUES (15, 'ART010', 1);
INSERT INTO LigCdes (nocde, refart, qtecde) VALUES (16, 'ART011', 1);
INSERT INTO LigCdes (nocde, refart, qtecde) VALUES (16, 'ART014', 3);
INSERT INTO LigCdes (nocde, refart, qtecde) VALUES (17, 'ART001', 1);
INSERT INTO LigCdes (nocde, refart, qtecde) VALUES (17, 'ART016', 1);
INSERT INTO LigCdes (nocde, refart, qtecde) VALUES (18, 'ART015', 1);
INSERT INTO LigCdes (nocde, refart, qtecde) VALUES (18, 'ART018', 2);
INSERT INTO LigCdes (nocde, refart, qtecde) VALUES (19, 'ART008', 2);
INSERT INTO LigCdes (nocde, refart, qtecde) VALUES (19, 'ART019', 1);
INSERT INTO LigCdes (nocde, refart, qtecde) VALUES (20, 'ART020', 3);
INSERT INTO LigCdes (nocde, refart, qtecde) VALUES (20, 'ART013', 2);

-- 6.7 LivraisonCom (30 livraisons — some with varied statuses)
INSERT INTO LivraisonCom (nocde, dateliv, livreur, modepay, etatliv)
VALUES (1, TO_DATE('2025-03-02 10:00:00', 'YYYY-MM-DD HH24:MI:SS'), 1, 'CARTE', 'LIVREE');

INSERT INTO LivraisonCom (nocde, dateliv, livreur, modepay, etatliv)
VALUES (2, TO_DATE('2025-03-02 14:00:00', 'YYYY-MM-DD HH24:MI:SS'), 2, 'ESPECES', 'LIVREE');

INSERT INTO LivraisonCom (nocde, dateliv, livreur, modepay, etatliv)
VALUES (3, TO_DATE('2025-03-03 09:00:00', 'YYYY-MM-DD HH24:MI:SS'), 3, 'CARTE', 'LIVREE');

INSERT INTO LivraisonCom (nocde, dateliv, livreur, modepay, etatliv)
VALUES (4, TO_DATE('2025-03-03 11:00:00', 'YYYY-MM-DD HH24:MI:SS'), 1, 'VIREMENT', 'LIVREE');

INSERT INTO LivraisonCom (nocde, dateliv, livreur, modepay, etatliv)
VALUES (5, TO_DATE('2025-03-04 09:30:00', 'YYYY-MM-DD HH24:MI:SS'), 4, 'ESPECES', 'LIVREE');

INSERT INTO LivraisonCom (nocde, dateliv, livreur, modepay, etatliv)
VALUES (6, TO_DATE('2025-03-04 15:00:00', 'YYYY-MM-DD HH24:MI:SS'), 5, 'CARTE', 'LIVREE');

INSERT INTO LivraisonCom (nocde, dateliv, livreur, modepay, etatliv)
VALUES (7, TO_DATE('2025-03-05 10:00:00', 'YYYY-MM-DD HH24:MI:SS'), 2, 'ESPECES', 'LIVREE');

INSERT INTO LivraisonCom (nocde, dateliv, livreur, modepay, etatliv)
VALUES (8, TO_DATE('2025-03-05 16:00:00', 'YYYY-MM-DD HH24:MI:SS'), 3, 'CARTE', 'LIVREE');

INSERT INTO LivraisonCom (nocde, dateliv, livreur, modepay, etatliv)
VALUES (9, TO_DATE('2025-03-06 09:00:00', 'YYYY-MM-DD HH24:MI:SS'), 1, 'VIREMENT', 'LIVREE');

INSERT INTO LivraisonCom (nocde, dateliv, livreur, modepay, etatliv)
VALUES (10, TO_DATE('2025-03-06 14:00:00', 'YYYY-MM-DD HH24:MI:SS'), 4, 'ESPECES', 'LIVREE');

INSERT INTO LivraisonCom (nocde, dateliv, livreur, modepay, etatliv)
VALUES (11, TO_DATE('2025-03-07 10:00:00', 'YYYY-MM-DD HH24:MI:SS'), 5, 'CARTE', 'LIVREE');

INSERT INTO LivraisonCom (nocde, dateliv, livreur, modepay, etatliv)
VALUES (12, TO_DATE('2025-03-07 12:00:00', 'YYYY-MM-DD HH24:MI:SS'), 2, 'ESPECES', 'LIVREE');

INSERT INTO LivraisonCom (nocde, dateliv, livreur, modepay, etatliv)
VALUES (13, TO_DATE('2025-03-08 10:30:00', 'YYYY-MM-DD HH24:MI:SS'), 3, 'CARTE', 'LIVREE');

INSERT INTO LivraisonCom (nocde, dateliv, livreur, modepay, etatliv)
VALUES (14, TO_DATE('2025-03-08 15:00:00', 'YYYY-MM-DD HH24:MI:SS'), 1, 'VIREMENT', 'LIVREE');

INSERT INTO LivraisonCom (nocde, dateliv, livreur, modepay, etatliv)
VALUES (15, TO_DATE('2025-03-09 09:00:00', 'YYYY-MM-DD HH24:MI:SS'), 4, 'ESPECES', 'LIVREE');

INSERT INTO LivraisonCom (nocde, dateliv, livreur, modepay, etatliv)
VALUES (16, TO_DATE('2025-03-09 12:00:00', 'YYYY-MM-DD HH24:MI:SS'), 5, 'CARTE', 'LIVREE');

INSERT INTO LivraisonCom (nocde, dateliv, livreur, modepay, etatliv)
VALUES (17, TO_DATE('2025-03-10 08:30:00', 'YYYY-MM-DD HH24:MI:SS'), 2, 'ESPECES', 'LIVREE');

INSERT INTO LivraisonCom (nocde, dateliv, livreur, modepay, etatliv)
VALUES (18, TO_DATE('2025-03-10 11:00:00', 'YYYY-MM-DD HH24:MI:SS'), 3, 'CARTE', 'LIVREE');

INSERT INTO LivraisonCom (nocde, dateliv, livreur, modepay, etatliv)
VALUES (19, TO_DATE('2025-03-11 09:30:00', 'YYYY-MM-DD HH24:MI:SS'), 1, 'VIREMENT', 'LIVREE');

INSERT INTO LivraisonCom (nocde, dateliv, livreur, modepay, etatliv)
VALUES (20, TO_DATE('2025-03-11 14:30:00', 'YYYY-MM-DD HH24:MI:SS'), 4, 'ESPECES', 'LIVREE');

-- Additional deliveries with varied statuses for testing
INSERT INTO LivraisonCom (nocde, dateliv, livreur, modepay, etatliv)
VALUES (21, TO_DATE('2025-03-12 09:00:00', 'YYYY-MM-DD HH24:MI:SS'), 1, 'ESPECES', 'EN_COURS');

INSERT INTO LivraisonCom (nocde, dateliv, livreur, modepay, etatliv)
VALUES (22, TO_DATE('2025-03-12 10:30:00', 'YYYY-MM-DD HH24:MI:SS'), 2, 'CARTE', 'EN_COURS');

INSERT INTO LivraisonCom (nocde, dateliv, livreur, modepay, etatliv)
VALUES (23, TO_DATE('2025-03-12 14:00:00', 'YYYY-MM-DD HH24:MI:SS'), 3, 'ESPECES', 'PLANIFIEE');

INSERT INTO LivraisonCom (nocde, dateliv, livreur, modepay, etatliv)
VALUES (24, TO_DATE('2025-03-13 09:00:00', 'YYYY-MM-DD HH24:MI:SS'), 4, 'VIREMENT', 'PLANIFIEE');

INSERT INTO LivraisonCom (nocde, dateliv, livreur, modepay, etatliv)
VALUES (25, TO_DATE('2025-03-13 11:00:00', 'YYYY-MM-DD HH24:MI:SS'), 5, 'ESPECES', 'PLANIFIEE');

INSERT INTO LivraisonCom (nocde, dateliv, livreur, modepay, etatliv)
VALUES (26, TO_DATE('2025-03-11 10:00:00', 'YYYY-MM-DD HH24:MI:SS'), 2, 'CARTE', 'ECHEC');

INSERT INTO LivraisonCom (nocde, dateliv, livreur, modepay, etatliv)
VALUES (27, TO_DATE('2025-03-10 15:00:00', 'YYYY-MM-DD HH24:MI:SS'), 5, 'ESPECES', 'ECHEC');

INSERT INTO LivraisonCom (nocde, dateliv, livreur, modepay, etatliv)
VALUES (28, TO_DATE('2025-03-09 14:00:00', 'YYYY-MM-DD HH24:MI:SS'), 1, 'CARTE', 'ANNULEE');

INSERT INTO LivraisonCom (nocde, dateliv, livreur, modepay, etatliv)
VALUES (29, TO_DATE('2025-03-08 16:00:00', 'YYYY-MM-DD HH24:MI:SS'), 3, 'ESPECES', 'RETARDEE');

INSERT INTO LivraisonCom (nocde, dateliv, livreur, modepay, etatliv)
VALUES (30, TO_DATE('2025-03-07 15:00:00', 'YYYY-MM-DD HH24:MI:SS'), 4, 'VIREMENT', 'RETARDEE');

-- ============================================================
-- 7. VIEWS FOR DASHBOARD ANALYTICS
-- ============================================================

-- View: Livraisons par livreur
CREATE OR REPLACE VIEW V_LIVRAISONS_PAR_LIVREUR AS
SELECT 
    p.idpers AS livreur_id,
    p.nompers || ' ' || p.prenompers AS livreur_nom,
    COUNT(l.nocde) AS total_livraisons,
    SUM(CASE WHEN l.etatliv = 'LIVREE' THEN 1 ELSE 0 END) AS livrees,
    SUM(CASE WHEN l.etatliv = 'ECHEC' THEN 1 ELSE 0 END) AS echecs,
    SUM(CASE WHEN l.etatliv = 'EN_COURS' THEN 1 ELSE 0 END) AS en_cours,
    SUM(CASE WHEN l.etatliv = 'PLANIFIEE' THEN 1 ELSE 0 END) AS planifiees,
    SUM(CASE WHEN l.etatliv = 'RETARDEE' THEN 1 ELSE 0 END) AS retardees,
    SUM(CASE WHEN l.etatliv = 'ANNULEE' THEN 1 ELSE 0 END) AS annulees,
    ROUND(SUM(CASE WHEN l.etatliv = 'LIVREE' THEN 1 ELSE 0 END) * 100.0 / NULLIF(COUNT(l.nocde), 0), 2) AS taux_reussite
FROM Personnel p
LEFT JOIN LivraisonCom l ON p.idpers = l.livreur
WHERE p.codeposte = 1
GROUP BY p.idpers, p.nompers, p.prenompers;

-- View: Livraisons par etat
CREATE OR REPLACE VIEW V_LIVRAISONS_PAR_ETAT AS
SELECT 
    etatliv AS etat,
    COUNT(*) AS nombre,
    ROUND(COUNT(*) * 100.0 / SUM(COUNT(*)) OVER (), 2) AS pourcentage
FROM LivraisonCom
GROUP BY etatliv;

-- View: Livraisons par client
CREATE OR REPLACE VIEW V_LIVRAISONS_PAR_CLIENT AS
SELECT 
    c.noclt AS client_id,
    c.nomclt || ' ' || c.prenomclt AS client_nom,
    c.villeclt AS ville,
    COUNT(l.nocde) AS total_livraisons,
    SUM(CASE WHEN l.etatliv = 'LIVREE' THEN 1 ELSE 0 END) AS livrees,
    SUM(CASE WHEN l.etatliv = 'ECHEC' THEN 1 ELSE 0 END) AS echecs
FROM Clients c
LEFT JOIN Commandes co ON c.noclt = co.noclt
LEFT JOIN LivraisonCom l ON co.nocde = l.nocde
GROUP BY c.noclt, c.nomclt, c.prenomclt, c.villeclt;

-- View: Livraisons du jour
CREATE OR REPLACE VIEW V_LIVRAISONS_DU_JOUR AS
SELECT 
    l.nocde,
    l.dateliv,
    l.etatliv,
    l.modepay,
    p.idpers AS livreur_id,
    p.nompers || ' ' || p.prenompers AS livreur_nom,
    p.telpers AS livreur_tel,
    c.noclt AS client_id,
    c.nomclt || ' ' || c.prenomclt AS client_nom,
    c.adrclt AS client_adresse,
    c.villeclt AS client_ville,
    c.telclt AS client_tel,
    co.datecde AS date_commande
FROM LivraisonCom l
JOIN Personnel p ON l.livreur = p.idpers
JOIN Commandes co ON l.nocde = co.nocde
JOIN Clients c ON co.noclt = c.noclt
WHERE TRUNC(l.dateliv) = TRUNC(SYSDATE);

-- View: Details complets des livraisons
CREATE OR REPLACE VIEW V_LIVRAISONS_DETAILS AS
SELECT 
    l.nocde,
    l.dateliv,
    l.etatliv,
    l.modepay,
    p.idpers AS livreur_id,
    p.nompers AS livreur_nom,
    p.prenompers AS livreur_prenom,
    p.telpers AS livreur_tel,
    c.noclt AS client_id,
    c.nomclt AS client_nom,
    c.prenomclt AS client_prenom,
    c.adrclt AS client_adresse,
    c.villeclt AS client_ville,
    c.code_postal,
    c.telclt AS client_tel,
    c.adrmail AS client_email,
    co.datecde AS date_commande,
    co.etatcde AS etat_commande,
    (SELECT COUNT(*) FROM LigCdes lc WHERE lc.nocde = l.nocde) AS nb_articles,
    (SELECT SUM(lc.qtecde) FROM LigCdes lc WHERE lc.nocde = l.nocde) AS total_quantite,
    (SELECT SUM(lc.qtecde * a.prixV) FROM LigCdes lc JOIN Articles a ON lc.refart = a.refart WHERE lc.nocde = l.nocde) AS montant_total
FROM LivraisonCom l
JOIN Personnel p ON l.livreur = p.idpers
JOIN Commandes co ON l.nocde = co.nocde
JOIN Clients c ON co.noclt = c.noclt;

-- View: Statistiques hebdomadaires
CREATE OR REPLACE VIEW V_STATS_HEBDO AS
SELECT 
    TO_CHAR(l.dateliv, 'IYYY') AS annee_iso,
    TO_CHAR(l.dateliv, 'IW') AS semaine_iso,
    TRUNC(l.dateliv, 'IW') AS debut_semaine,
    COUNT(*) AS total_livraisons,
    SUM(CASE WHEN l.etatliv = 'LIVREE' THEN 1 ELSE 0 END) AS livrees,
    SUM(CASE WHEN l.etatliv = 'ECHEC' THEN 1 ELSE 0 END) AS echecs,
    ROUND(SUM(CASE WHEN l.etatliv = 'LIVREE' THEN 1 ELSE 0 END) * 100.0 / NULLIF(COUNT(*), 0), 2) AS taux_reussite
FROM LivraisonCom l
GROUP BY TO_CHAR(l.dateliv, 'IYYY'), TO_CHAR(l.dateliv, 'IW'), TRUNC(l.dateliv, 'IW')
ORDER BY annee_iso DESC, semaine_iso DESC;

-- ============================================================
-- COMMIT
-- ============================================================
COMMIT;
