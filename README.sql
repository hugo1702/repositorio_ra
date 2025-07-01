/* 1. Desarrolle una consulta que liste los países por región, los datos que debe mostrar son: el 
código de la región y nombre de la región con los nombres de sus países. */

SELECT r.region_id, r.region_name, c.country_name
FROM regions r
JOIN countries c ON r.region_id = r.region_id
ORDER BY r.region_id;

/* 2. Realice una consulta que muestre el código, nombre, apellido, inicio y fin del historial de 
trabajo de los empleados. */

SELECT e.employee_id, e.first_name, e.last_name, jh.start_date, jh.end_date
FROM job_history jh
JOIN employees e ON jh.employee_id = e.employee_id
ORDER BY e.employee_id;

/* 3. Elabore una consulta que liste nombre del trabajo y el salario de los empleados que son 
manager, cuyo código es 100 o 125 y cuyo salario sea mayor de 6000. */

SELECT j.job_title, e.salary
FROM employees e
JOIN jobs j ON e.job_id = j.job_id
WHERE e.employee_id IN (100, 125)
AND e.salary > 6000;

/* 4. Desarrolle una consulta que liste el código de la localidad, la ciudad y el nombre del 
departamento de únicamente de los que se encuentran fuera de Estados Unidos (US). */

SELECT l.location_id, l.city, d.department_name
FROM departments d
JOIN locations l ON d.location_id = l.location_id
JOIN countries c ON l.country_id = c.country_id
WHERE c.country_id <> 'US';

/*5. Revisar manuales de SQL y entregar 2 consultas con tema libre, donde abarque lo siguiente 
(2 consultas por cada punto) */

/* A.GROUP BY */

/* Promedio de salario por departamento*/
SELECT department_id, AVG(salary) AS promedio_salario
FROM employees
GROUP BY department_id;

/* Número de empleados por trabajo */ 
SELECT job_id, COUNT(*) AS cantidad
FROM employees
GROUP BY job_id;

/* B. HAVING */

/*Departamentos con salario promedio mayor a 8000*/
SELECT department_id, AVG(salary) AS promedio
FROM employees
GROUP BY department_id
HAVING AVG(salary) > 8000;

/* Trabajos con más de 5 empleados*/
SELECT job_id, COUNT(*) AS total_empleados 
FROM employees
GROUP BY job_id
HAVING COUNT(*) > 5;

/* C. DISTINCT*/

/*Lista de todos los títulos de trabajo únicos*/
SELECT DISTINCT job_id
FROM employees;

/*Ciudades distintas donde hay departamentos*/ 
SELECT DISTINCT city
FROM locations;

/* D. UNION*/

/*Lista de todos los job_id en employees y job_history*/
SELECT job_id FROM employees
UNION
SELECT job_id FROM job_history;

/*Lista de paises y regiones disitintas*/
SELECT country_name FROM countries
UNION
SELECT region_name FROM regions;

/* E. MINUS */

/* job_id actuales que no aparecen en el historial*/
SELECT job_id FROM employees
MINUS
SELECT job_id FROM job_history;

/*Empleados actuales que no tienen historial laboral*/ 
SELECT employee_id FROM employees
MINUS
SELECT employee_id FROM job_history;

/* F.UNION_ALL */

/* Empleados con salario bajo y alto */ 
SELECT first_name, salary
FROM employees
WHERE salary < 3000
UNION ALL
SELECT first_name, salary
FROM employees
WHERE salary > 10000;

/* Los nombres de empleados que ganan menos de 2000 y también los que trabajan en el departamento 90.*/
SELECT first_name, salary, department_id FROM employees WHERE salary < 2000
UNION ALL
SELECT first_name, salary, department_id FROM employees WHERE department_id = 90;

/* G. SUM */

/*Suma total de salarios:*/
SELECT SUM(salary) AS total_salarios
FROM employees;

/* Suma de salarios por trabajo: */
SELECT job_id, SUM(salary) AS total
FROM employees
GROUP BY job_id;

/* H. COUNT*/

/* Número total de empleados*/
SELECT COUNT(*) AS total_empleados
FROM employees;

/*  Número de empleados por departamento*/
SELECT department_id, COUNT(*) AS cantidad
FROM employees
GROUP BY department_id;

/* I. AVG */ 

/*Promedio general de salarios */
SELECT AVG(salary) AS promedio_salarial
FROM employees;

/* Promedio de todos los salarios*/
SELECT AVG(salary) AS promedio_general
FROM employees;

/*  J. LIKE */ 

/* Empleados cuyo apellido comienza con 'S' */ 
SELECT first_name, last_name
FROM employees
WHERE last_name LIKE 'S%';

/*Empleados cuyo nombre contiene una 'a'*/
SELECT first_name, last_name
FROM employees
WHERE first_name LIKE '%a%';

/* 6. Hacer una consulta SQL donde se muestren todos los empleados e indique quién es su Jefe.*/ 

SELECT  e.first_name || ' ' || e.last_name AS empleado,
        m.first_name || ' ' || m.last_name AS jefe
FROM employees e
LEFT JOIN employees m ON e.manager_id = m.employee_id
ORDER BY e.employee_id;

/* 7. La consulta debe mostrar nombre y apellido del empleado y en el mismo registro mostrar 
nombre y apellido del jefe */ 

SELECT  e.first_name || ' ' || e.last_name AS empleado,
        m.first_name || ' ' || m.last_name AS jefe
FROM employees e
LEFT JOIN employees m ON e.manager_id = m.employee_id
ORDER BY e.employee_id;

/* 8. Desarrolle una consulta que liste el código, nombre y apellido de los empleados y sus 
respectivos jefes con título */

SELECT 
    e.employee_id AS codigo_empleado,
    e.first_name || ' ' || e.last_name AS nombre_empleado,
    m.first_name || ' ' || m.last_name AS nombre_jefe,
    j1.job_title AS titulo_empleado,
    j2.job_title AS titulo_jefe
FROM employees e
LEFT JOIN employees m ON e.manager_id = m.employee_id
JOIN jobs j1 ON e.job_id = j1.job_id
LEFT JOIN jobs j2 ON m.job_id = j2.job_id;  

/*9. Desarrolle una consulta que liste los países por región, los datos que debe mostrar son: el 
código de la región y nombre de la región con los nombres de sus países.  */ 

SELECT 
    r.region_id,
    r.region_name,
    c.country_name
FROM regions r
JOIN countries c ON r.region_id = c.region_id
ORDER BY r.region_id, c.country_name;

/* 10. Realice una consulta que muestre el código, nombre, apellido, inicio y fin del historial de 
trabajo de los empleados. */

SELECT 
    e.employee_id,
    e.first_name,
    e.last_name,
    jh.start_date,
    jh.end_date
FROM employees e
LEFT JOIN job_history jh ON e.employee_id = jh.employee_id
ORDER BY e.employee_id;

/* 11. Elabore una consulta que muestre el nombre y apellido del empleado con titulo Empleado, el 
salario, porcentaje de comisión, la comisión y salario total.  */ 
/*nota: como tal no existen el titulo empleado, por esa razon no se retornan registros */ 

SELECT 
    e.first_name,
    e.last_name,
    e.salary,
    e.commission_pct,
    (e.salary * NVL(e.commission_pct, 0)) AS comision,
    (e.salary + (e.salary * NVL(e.commission_pct, 0))) AS salario_total
FROM employees e
JOIN jobs j ON e.job_id = j.job_id
WHERE j.job_title = 'Empleado';

/* 12. Elabore una consulta que liste nombre del trabajo y el salario de los empleados que son 
manager, cuyo código es 100 o 125 y cuyo salario sea mayor de 6000.  */ 

SELECT j.job_title AS nombre_trabajo, e.salary AS salario
FROM employees e
JOIN jobs j ON e.job_id = j.job_id
WHERE e.manager_id IN (100, 125)
AND e.salary > 6000;

/*13. Realice una consulta que muestres el código de la región, nombre de la región y el nombre 
de los países que se encuentran en “Asia”  */ 

SELECT r.region_id, r.region_name, c.country_name 
FROM regions r
JOIN countries c ON r.region_id = c.region_id
WHERE r.region_name = 'Asia';

/* 14. Elabore una consulta que liste el código de la región y nombre de la región, código de la 
localidad, la ciudad, código del país y nombre del país, de solamente de las localidades 
mayores a 2400. */ 

SELECT r.region_id, r.region_name, l.location_id, l.city, c.country_id, c.country_name
FROM regions r
JOIN countries c ON r.region_id = c.region_id
JOIN locations l ON c.country_id = l.country_id
WHERE l.location_id > 2400;

/* 15. Desarrolle una consulta donde muestre el código de región con un alias de Región, el 
nombre de la región con una etiqueta Nombre Región, que muestre una cadena string 
(concatenación) que diga la siguiente frase “Código País:CA Nombre: Canadá“ ,CA es el 
código de país y Canadá es el nombre del país con etiqueta País, el código de localización 
con etiqueta Localización, la dirección de calle con etiqueta Dirección y el código postal con 
etiqueta“ Código Postal”, esto a su vez no deben aparecer código postal que sean nulos.  */ 

SELECT
    r.region_id AS "Región",
    r.region_name AS "Nombre Región",
    'Código País:' || c.country_id || ' Nombre: ' || c.country_name AS "País",
    l.location_id AS "Localización",
    l.street_address AS "Dirección",
    l.postal_code AS "Código Postal"
FROM regions r
JOIN countries c ON r.region_id = c.region_id
JOIN locations l ON c.country_id = l.country_id
WHERE l.postal_code IS NOT NULL;

/* 16. Desarrolle una consulta que muestre el salario promedio de los empleados de los 
departamentos 30 y 80. */ 

SELECT department_id, ROUND(AVG(salary), 2) AS salario_promedio
FROM employees
WHERE department_id IN (30, 80)
GROUP BY department_id;

/* 17. Desarrolle una consulta que muestre el nombre de la región, el nombre del país, el estado de 
la provincia, el código de los empleados que son manager, el nombre y apellido del empleado 
que es manager de los países del reino Unido (UK), Estados Unidos de América (US), 
respectivamente de los estados de la provincia de Washington y Oxford.  */ 

SELECT r.region_name, c.country_name, l.state_province, e.employee_id AS id_manager, e.first_name, e.last_name
FROM employees e
JOIN departments d ON e.employee_id = d.manager_id
JOIN locations l ON d.location_id = l.location_id
JOIN countries c ON l.country_id = c.country_id
JOIN regions r ON c.region_id = r.region_id
WHERE c.country_id IN ('UK', 'US')
AND l.state_province IN ('Oxford', 'Washington');

/* 18. Realice una consulta que muestre el nombre y apellido de los empleados que trabajan para 
departamentos que están localizados en países cuyo nombre comienza con la letra C, que 
muestre el nombre del país. */ 

SELECT e.first_name, e.last_name, c.country_name
FROM employees e
JOIN departments d ON e.department_id = d.department_id
JOIN locations l ON d.location_id = l.location_id
JOIN countries c ON l.country_id = c.country_id
WHERE c.country_id LIKE 'C%';

/* 19. Desarrolle una consulta que muestre el código, el nombre y apellido separado por coma con 
titulo de encabezado Nombre Completo, el salario con titulo Salario, el código de 
departamento con titulo Código de Departamento y el nombre de departamento al que 
pertenece con titulo Descripción, únicamente se desean consultas los que pertenezcan al 
departamento de IT y ordenar la información por salario descendentemente.  */ 

SELECT  e.employee_id AS "Código", 
        e.first_name || ', ' || e.last_name AS "Nombre Completo",
        e.salary AS "Salario",
        d.department_id AS "Código Departamento",
        d.department_name AS "Descripción"
FROM employees e
JOIN departments d ON e.department_id = d.department_id
WHERE d.department_name = 'IT'
ORDER BY e.salary DESC;

/*20. Desarrolle una consulta que muestre el código del departamento con titulo Código del 
departamento, que cuente los empleados agrupados por departamentos, ordenados por 
código de departamento */ 

SELECT 
    department_id AS "Código del departamento",
    COUNT(*) AS "Total Empleados"
FROM employees
GROUP BY department_id
ORDER BY department_id;

/* 21.Realicé una consulta que muestre solo los nombres de los empleados que se repiten.*/

SELECT first_name 
FROM employees GROUP BY first_name
HAVING COUNT(*) > 1;

/* 22. Desarrolle una consulta que muestre solo los nombres de los empleados que no se repiten. */ 

SELECT first_name 
FROM employees 
GROUP BY first_name
HAVING COUNT(*) = 1;

/*23.  Realice una consulta que muestre el número de países por región, la consulta debe mostrar 
el código y nombre de la región así como el número de países de cada región, ordenando el 
resultado por la región que tenga mayor número de países.  */

SELECT r.region_id, r.region_name, COUNT(c.country_id) AS "Número de Países"
FROM regions r
JOIN countries c ON r.region_id = c.region_id
GROUP BY r.region_id, r.region_name
ORDER BY COUNT(c.country_id) DESC;

/* 24. Desarrolle una consulta que liste los códigos de puestos con el número de empleados que 
pertenecen a cada puesto, ordenados por número de empleados: los puestos que tienen más 
empleados aparecen primero. */ 

SELECT job_id AS "Código de Empleado", COUNT(*) AS "Número de Empleados"
FROM employees GROUP BY job_id
ORDER BY COUNT(*) DESC;

/*25. Desarrolle una consulta que muestre el número de empleados por departamento, ordenados 
alfabéticamente por nombre de departamento.  */

SELECT d.department_name, COUNT(e.employee_id) AS "Número de Empleados"
FROM departments d 
LEFT JOIN employees e ON d.department_id = e.department_id
GROUP BY d.department_name
ORDER BY d.department_name;

/* 26. Realice una consulta que muestre el número de departamentos por región. */ 

SELECT r.region_name, COUNT(d.department_id) AS "Número de departamentos"
FROM regions r 
JOIN countries c ON r.region_id = c.region_id
JOIN locations l ON c.country_id = l.country_id
JOIN departments d ON l.location_id = d.location_id
GROUP BY r.region_name;

/* 27. Realice una consulta que  muestre el salario que paga cada departamento (sin incluir 
comisión), ordenado descendentemente por salario pagado. Se mostrara el código y nombre 
del departamento y el salario que paga.*/

SELECT d.department_id, d.department_name, SUM(e.salary) AS salario_total
FROM departments d
JOIN employees e ON e.department_id = d.department_id
GROUP BY d.department_id, d.department_name
ORDER BY SUM(e.salary) DESC;

/*28.  Desarrolle una consulta que muestre el código del departamento con título “Código del 
Departamento”, El código del trabajo con título “Puesto de trabajo” y que cuente los 
empleados de los departamentos 50 y 80, ordenando el resultado por departamento y puesto 
de trabajo.  */ 

SELECT e.department_id AS "Código de Departamento", e.job_id AS "Puesto de trabajo", COUNT(*) AS "Número de Empleados"
FROM employees e 
WHERE e.department_id IN (50, 80)
GROUP BY e.department_id, e.job_id
ORDER BY e.department_id, e.job_id;

/*29. Elabore una consulta que muestre el código del departamento con titulo “Código del 
departamento”, que cuente los empleados por departamento de aquellos departamentos que 
tengan más de 10 empleados.  */ 

SELECT department_id AS "Código del Departamento", COUNT(*) AS "Número de empleados"  
FROM employees 
GROUP BY department_id    
HAVING COUNT(*) > 10;

/* 30. Desarrolle una consulta que liste el apellido, el nombre y salario del empleado con el salario 
mayor de todos los departamentos. */

SELECT e.last_name, e.first_name, e.salary
FROM employees e
WHERE e.salary = (SELECT MAX(salary) FROM employees WHERE department_id = e.department_id)
AND ROWNUM = 1
ORDER BY  e.salary DESC;

/* 31.Desarrolle una consulta que muestre código de departamento, el nombre y apellido de los 
empleados de únicamente de los departamentos en donde existen empleados con nombre 
‘Jonh’  */ 
SELECT e.department_id AS "Código de Departamento",
       e.first_name AS "Nombre",
       e.last_name AS "Apellido"
FROM employees e
WHERE e.department_id IN (
    SELECT department_id
    FROM employees
    WHERE first_name = 'John'
);

/* 32. Desarrolle una consulta que liste el código de departamento, nombre, apellido y salario de 
únicamente de los empleados con máximo salario en cada departamento.  */

SELECT e.department_id, e.first_name, e.last_name, e.salary 
FROM employees e
WHERE e.salary = (SELECT MAX(salary) FROM employees WHERE department_id = e.department_id);

/* 33.  Elabore una consulta que muestre el código del departamento, el nombre de departamento y 
el salario máximo de cada departamento.  */

SELECT d.department_id, d.department_name, MAX(e.salary) AS "Salario maximo"
FROM departments d 
JOIN employees e ON d.department_id = e.department_id
GROUP BY d.department_id, d.department_name;

/* 34. Encuentra todos los registros en la tabla empleados que contengan un valor que ocurre dos 
veces en una columna dada.  */
/* Nota: en este caso nignuno de los registros ocurre 2 veces por lo tanto no se muestran resultados*/

SELECT *
FROM employees e
WHERE e.email IN (
    SELECT email
    FROM employees
    GROUP BY email
    HAVING COUNT(*) = 2
);  

/* 35. Realice una consulta que liste los empleados que están en departamentos que tienen menos 
de 10 empleados.  */

SELECT e.first_name || ' ' || e.last_name AS "EMPLEADOS"
FROM employees e
WHERE e.department_id IN (
    SELECT department_id
    FROM employees
    GROUP BY department_id
    HAVING COUNT(*) < 10
);

/*36. Desarrolle una consulta que muestre el mayor salario entre los empleados que trabajan en el 
departamento 30 (DEPARTAMENTO_ID) y que empleados ganan ese salario. */

SELECT employee_id, first_name, last_name, department_id, salary
FROM employees
WHERE department_id = 30
AND salary = (SELECT MAX(salary) FROM employees WHERE department_id =30);

/* 37. Elabore una consulta que muestre los departamentos en donde no exista ningún empleado. */

SELECT d.department_id, d.department_name
FROM departments d 
LEFT JOIN employees e ON d.department_id = e.department_id
WHERE e.employee_id IS NULL;

/*38. Desarrolle una consulta que muestre a todos los empleados que no estén trabajando en el 
departamento 30 y que ganen más que todos los empleados que trabajan en el 
departamento 30.  */ 

SELECT first_name, last_name, salary, department_id
FROM employees
WHERE department_id <> 30
AND salary > ALL (SELECT salary FROM employees WHERE department_id = 30);

/* 39. Realice una consulta que muestre los empleados que son gerentes (GERENTE_ID)  y el 
número de empleados subordinados a cada uno, ordenados descendentemente por número 
de subordinado. Excluya a los gerentes que tienen 5 empleados subordinados o menos.  */ 

SELECT e.employee_id AS gerente_id, e.first_name || ' ' || e.last_name AS nombre_gerente,
    COUNT(s.employee_id) AS numero_subordinados
FROM employees e
JOIN employees s ON e.employee_id = s.manager_id
GROUP BY e.employee_id, e.first_name, e.last_name
HAVING COUNT(s.employee_id) > 5
ORDER BY numero_subordinados DESC;

/*40. Generar consulta de los empleados cuyo apellido comience con la letra a. */

SELECT *
FROM employees
WHERE last_name LIKE 'A%';

/* 41. Genere consulta la cual muestre a los empleados que su salario se encuentre dentro del 
rango de 5000 a 15000.  */ 

SELECT first_name, last_name, salary FROM employees WHERE salary BETWEEN 5000 AND 15000;

/* 42. Genere consulta el cual muestre el salario + comisión anual por empleado */ 

SELECT employee_id, first_name, last_name, salary, 
(salary * 12) + NVL(salary * commission_pct * 12, 0) AS salario_comision_anual
FROM employees;

/*43. Consulta que muestre el departamento con mayor cantidad de empleados y el departamento 
con la menor cantidad de la tabla de empleados.*/

SELECT department_id, cantidad_empleados
FROM (
    SELECT department_id, COUNT(*) AS cantidad_empleados
    FROM employees
    GROUP BY department_id
    ORDER BY cantidad_empleados DESC
) WHERE ROWNUM = 1
UNION
SELECT department_id, cantidad_empleados
FROM (
    SELECT department_id, COUNT(*) AS cantidad_empleados
    FROM employees
    GROUP BY department_id
    ORDER BY cantidad_empleados ASC
) WHERE ROWNUM = 1;

/* 44. Genera consulta en la cual se muestre el nombre de todos los empleados, su fecha de 
contratación, el nombre del departamento al que corresponden y el nombre de su gerente. */ 

SELECT e.first_name || ' ' || e.last_name AS nombre_empleado,
    e.hire_date,
    d.department_name,
    g.first_name || ' ' || g.last_name AS nombre_gerente
FROM employees e
LEFT JOIN departments d ON e.department_id = d.department_id
LEFT JOIN employees g ON e.manager_id = g.employee_id;

/* 45. Genere una consulta para obtener departamento y el salario máximo del departamento para 
todos los departamentos cuyo salario máximo sea menor al salario promedio de todos los 
empleados.*/

SELECT department_id, MAX(salary) AS salario_maximo
FROM employees
GROUP BY department_id
HAVING MAX(salary) < (SELECT AVG(salary) FROM employees);

/*46. Genere una consulta para obtener departamento y el salario máximo del departamento para 
todos los departamentos cuyo salario máximo sea menor que el salario promedio en todos 
los demás departamentos.  */
SELECT d.department_name, MAX(e.salary) AS salario_maximo
FROM departments d
JOIN employees e ON d.department_id = e.department_id
GROUP BY d.department_id, d.department_name
HAVING MAX(e.salary) < (SELECT AVG(e2.salary) FROM employees e2
        WHERE e2.department_id IS NOT NULL AND e2.department_id <> d.department_id);

/* 47. Genere una consulta la cual muestre la lista de los empleados, el id de la localidad, la ciudad 
y el nombre del departamento de únicamente de los que se encuentran fuera de Estados 
Unidos (US).  */

SELECT e.first_name || ' ' || e.last_name AS nombre_empleado,
       l.location_id, l.city, d.department_name
FROM employees e
JOIN departments d ON e.department_id = d.department_id
JOIN locations l ON d.location_id = l.location_id
JOIN  countries c ON l.country_id = c.country_id
WHERE c.country_id <> 'US';

/*48. Muestre el salario más alto, más bajo, salario total, salario promedio y la cantidad de 
empleados, cada tipo de puesto que se tiene en la organización. Redondee los resultados a 
dos decimales. */

SELECT job_id,
    ROUND(MAX(salary), 2) AS salario_mas_alto,
    ROUND(MIN(salary), 2) AS salario_mas_bajo,
    ROUND(SUM(salary), 2) AS salario_total,
    ROUND(AVG(salary), 2) AS salario_promedio,
    COUNT(*) AS cantidad_empleados
FROM employees
GROUP BY job_id;

/* 49. Genere una consulta que muestre el nombre del departamento y los empleados que se 
encuentren dentro de ese departamento, de la siguiente manera. */

SELECT d.department_name AS departamento,
    LISTAGG(e.first_name || ' ' || e.last_name, ', ') 
        WITHIN GROUP (ORDER BY e.first_name, e.last_name) AS empleados
FROM employees e
JOIN departments d ON e.department_id = d.department_id
GROUP BY d.department_name
ORDER BY d.department_name;


/* ----------------------------------------------------------------------------------------------------------------*/

/* PL/SQL Básico*/

/*1.  Crear un bloque anónimo donde reciba como parámetro el ID de departamento y me regrese 
el nombre del departamento con el promedio de salarios de dicho departamento.*/

SET SERVEROUTPUT ON;
DECLARE
    v_department_id departments.department_id%type := &id_department;
    v_department_name departments.department_name%type;
    v_avg_salary NUMBER(10,2);
BEGIN 
    SELECT department_name
    INTO v_department_name
    FROM departments
    WHERE department_id = v_department_id;
    
    SELECT AVG(salary)
    INTO v_avg_salary
    FROM employees
    WHERE department_id = v_department_id;
    
    DBMS_OUTPUT.PUT_LINE('Departamento: ' || v_department_name);
     DBMS_OUTPUT.PUT_LINE('Promedio de salario: ' || TO_CHAR(v_avg_salary, '99999.99'));
END;

/*2. Crear un bloque anónimo donde imprima una seria de 2 en 2 hasta el 100  */  
BEGIN
   FOR i IN 2 .. 100 LOOP
      IF MOD(i, 2) = 0 THEN
         DBMS_OUTPUT.PUT_LINE(i);
      END IF;
   END LOOP;
END;

/*3. Crear un bloque anónimo donde se reciba el id de empleado y me imprima nombre, apellido, 
correo, teléfono, nombre de departamento, código postal, dirección, ciudad, nombre de país y 
nombre de la región. */ 
/*nota: el id es asignado por el usuario para hacer la consulta*/

SET SERVEROUTPUT ON;

DECLARE
    v_emp_id employees.employee_id%TYPE := &id_empleado;
    v_nombre employees.first_name%TYPE;
    v_apellido employees.last_name%TYPE;
    v_email employees.email%TYPE;
    v_telefono employees.phone_number%TYPE;
    v_depto_nombre departments.department_name%TYPE;
    v_cod_postal locations.postal_code%TYPE;
    v_direccion locations.street_address%TYPE;
    v_ciudad locations.city%TYPE;
    v_pais_nombre countries.country_name%TYPE;
    v_region_nombre regions.region_name%TYPE;
BEGIN
    SELECT e.first_name, e.last_name, e.email, e.phone_number,
           d.department_name, l.postal_code, l.street_address, l.city,
           c.country_name, r.region_name
    INTO   v_nombre, v_apellido, v_email, v_telefono,
           v_depto_nombre, v_cod_postal, v_direccion, v_ciudad,
           v_pais_nombre, v_region_nombre
    FROM employees e
    JOIN departments d ON e.department_id = d.department_id
    JOIN locations l ON d.location_id = l.location_id
    JOIN countries c ON l.country_id = c.country_id
    JOIN regions r ON c.region_id = r.region_id
    WHERE e.employee_id = v_emp_id;

    DBMS_OUTPUT.PUT_LINE('Nombre completo: ' || v_nombre || ' ' || v_apellido);
    DBMS_OUTPUT.PUT_LINE('Correo: ' || v_email);
    DBMS_OUTPUT.PUT_LINE('Telefono: ' || v_telefono);
    DBMS_OUTPUT.PUT_LINE('Departamento: ' || v_depto_nombre);
    DBMS_OUTPUT.PUT_LINE('Dirección: ' || v_direccion || ', CP: ' || v_cod_postal);
    DBMS_OUTPUT.PUT_LINE('Ciudad: ' || v_ciudad);
    DBMS_OUTPUT.PUT_LINE('Pais: ' || v_pais_nombre);
    DBMS_OUTPUT.PUT_LINE('Region: ' || v_region_nombre);
END;

/*4.  Crear un bloque anónimo donde se reciba como parámetro el id de la persona e imprima por 
pantalla el nombre de la persona y el nombre de su jefe 

nota: para que no marque error al buscar un registro que no existe se le podria agregar una excepcion*/ 

SET SERVEROUTPUT ON;

DECLARE
    v_emp_id employees.employee_id%TYPE := &id_persona;
    v_nombre VARCHAR2(100);
    v_jefe_nombre VARCHAR2(100);
BEGIN

    SELECT e.first_name || ' ' || e.last_name,
           NVL(m.first_name || ' ' || m.last_name, 'No tiene jefe')
    INTO v_nombre, v_jefe_nombre
    FROM employees e
    LEFT JOIN employees m ON e.manager_id = m.employee_id
    WHERE e.employee_id = v_emp_id;

    DBMS_OUTPUT.PUT_LINE('Empleado: ' || v_nombre);
    DBMS_OUTPUT.PUT_LINE('Jefe: ' || v_jefe_nombre);
END;

