// Ejecutar dentro del contenedor. Repetible sobre el volumen del laboratorio.
try {
    const status = rs.status();
    if (status.set !== "rs0") {
        throw new Error("Replica set inesperado: " + status.set);
    }
} catch (error) {
    if (error.code !== 94 && error.codeName !== "NotYetInitialized") {
        throw error;
    }
    assert.commandWorked(rs.initiate({
        _id: "rs0",
        members: [{ _id: 0, host: "localhost:27018" }]
    }));
}

let primary = false;
for (let attempt = 0; attempt < 60; attempt++) {
    if (db.hello().isWritablePrimary) {
        primary = true;
        break;
    }
    sleep(500);
}
if (!primary) {
    throw new Error("rs0 no eligió un primary dentro del plazo");
}
print("Replica set rs0 listo");
