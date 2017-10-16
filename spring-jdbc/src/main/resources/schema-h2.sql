
CREATE TABLE departamento (
  id IDENTITY PRIMARY KEY,
  nombre VARCHAR(64) NOT NULL
);

CREATE TABLE empleado (
  id IDENTITY PRIMARY KEY,
  nombre VARCHAR(64) NOT NULL,
  departamento_id INT,
  CONSTRAINT FK_EMP_DEPT_ID FOREIGN KEY(departamento_id) REFERENCES departamento(id)
);