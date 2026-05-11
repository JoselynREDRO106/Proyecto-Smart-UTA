window.SmartCampusGrafo = {
  async render(canvasSelector, camino = null) {
    const canvas = document.querySelector(canvasSelector);
    if (!canvas) return;

    const ctx = canvas.getContext("2d");
    const rect = canvas.getBoundingClientRect();
    const width = Math.max(rect.width, 320);
    const height = 360;
    canvas.width = width * window.devicePixelRatio;
    canvas.height = height * window.devicePixelRatio;
    ctx.setTransform(window.devicePixelRatio, 0, 0, window.devicePixelRatio, 0, 0);

    try {
      const [nodosData, conexionesData] = await Promise.all([
        window.SmartCampusAPI.parseJson(await window.SmartCampusAPI.listarNodosRutas()),
        window.SmartCampusAPI.parseJson(await window.SmartCampusAPI.listarConexionesRutas())
      ]);
      const nodos = this.layout(Array.isArray(nodosData) ? nodosData : nodosData.nodos || [], width, height);
      const conexiones = Array.isArray(conexionesData) ? conexionesData : conexionesData.conexiones || [];
      const resaltado = Array.isArray(camino) ? camino.map((p) => p.id ?? p) : [];

      ctx.clearRect(0, 0, width, height);
      ctx.lineWidth = 2;
      conexiones.forEach((conexion) => {
        const origen = nodos.find((n) => n.id === (conexion.origenId ?? conexion.origen ?? conexion.punto_origen_id));
        const destino = nodos.find((n) => n.id === (conexion.destinoId ?? conexion.destino ?? conexion.punto_destino_id));
        if (!origen || !destino) return;
        ctx.strokeStyle = "#d9e2ec";
        ctx.beginPath();
        ctx.moveTo(origen.x, origen.y);
        ctx.lineTo(destino.x, destino.y);
        ctx.stroke();
        if (conexion.distanciaMetros || conexion.distancia_metros) {
          ctx.fillStyle = "#667085";
          ctx.font = "600 12px Inter";
          ctx.fillText(`${conexion.distanciaMetros || conexion.distancia_metros}m`, (origen.x + destino.x) / 2, (origen.y + destino.y) / 2);
        }
      });

      if (resaltado.length > 1) {
        ctx.strokeStyle = "#e87a2a";
        ctx.lineWidth = 5;
        ctx.beginPath();
        resaltado.forEach((id, index) => {
          const punto = nodos.find((n) => n.id === id);
          if (!punto) return;
          index ? ctx.lineTo(punto.x, punto.y) : ctx.moveTo(punto.x, punto.y);
        });
        ctx.stroke();
      }

      nodos.forEach((punto) => {
        ctx.fillStyle = "#0b3b5f";
        ctx.beginPath();
        ctx.arc(punto.x, punto.y, 14, 0, Math.PI * 2);
        ctx.fill();
        ctx.fillStyle = "#17212b";
        ctx.font = "600 13px Inter";
        ctx.fillText(punto.nombre || punto.label || `Nodo ${punto.id}`, punto.x + 18, punto.y + 4);
      });
    } catch (error) {
      ctx.clearRect(0, 0, width, height);
      ctx.fillStyle = "#842029";
      ctx.font = "600 14px Inter";
      ctx.fillText(error.message, 20, 40);
    }
  },
  layout(nodos, width, height) {
    const radius = Math.min(width, height) * 0.36;
    const centerX = width / 2;
    const centerY = height / 2;
    return nodos.map((nodo, index) => {
      const angle = (Math.PI * 2 * index) / Math.max(nodos.length, 1) - Math.PI / 2;
      return {
        ...nodo,
        id: nodo.id,
        x: Number(nodo.x ?? nodo.posX ?? centerX + Math.cos(angle) * radius),
        y: Number(nodo.y ?? nodo.posY ?? centerY + Math.sin(angle) * radius)
      };
    });
  }
};
