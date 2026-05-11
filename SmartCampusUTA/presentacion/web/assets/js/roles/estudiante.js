document.addEventListener("DOMContentLoaded", async () => {
  const acciones = document.querySelector("[data-pila]");
  if (!acciones) return;

  acciones.innerHTML = window.SmartCampusUtils.loading("Cargando historial de tramites...");
  try {
    const historial = await window.SmartCampusAPI.parseJson(await window.SmartCampusAPI.obtenerHistorialTramites());
    window.SmartCampusPila.render(acciones, Array.isArray(historial) ? historial : [historial].filter(Boolean));
  } catch (error) {
    window.SmartCampusUtils.showAlert(acciones, error.message, "danger");
  }
});
