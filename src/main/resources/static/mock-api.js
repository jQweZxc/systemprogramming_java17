// mock-api.js - Заглушки для API для тестирования фронтенда

// Переопределяем fetch для имитации API
const originalFetch = window.fetch;

window.fetch = async function(url, options) {
    // Имитируем задержку сети
    await new Promise(resolve => setTimeout(resolve, 300));
    
    // Обрабатываем разные endpoints
    if (url.includes('/api/products')) {
        return new Response(JSON.stringify([
            { id: 1, title: "Продукт 1", cost: 1000 },
            { id: 2, title: "Продукт 2", cost: 2000 },
            { id: 3, title: "Продукт 3", cost: 1500 }
        ]), { status: 200 });
    }
    
    if (url.includes('/api/buses')) {
        return new Response(JSON.stringify([
            { id: 1, model: "Mercedes Sprinter", route: { id: 1 }, currentLoad: 32 },
            { id: 2, model: "Volkswagen Crafter", route: { id: 1 }, currentLoad: 45 },
            { id: 3, model: "Ford Transit", route: { id: 2 }, currentLoad: 28 }
        ]), { status: 200 });
    }
    
    if (url.includes('/api/stops')) {
        return new Response(JSON.stringify([
            { id: 1, name: "Центральная", lat: 55.7558, lon: 37.6176 },
            { id: 2, name: "Северная", lat: 55.7658, lon: 37.6276 },
            { id: 3, name: "Южная", lat: 55.7458, lon: 37.6076 }
        ]), { status: 200 });
    }
    
    if (url.includes('/api/routes')) {
        return new Response(JSON.stringify([
            { 
                id: 1, 
                stops: [
                    { id: 1, name: "Центральная" },
                    { id: 2, name: "Северная" },
                    { id: 3, name: "Южная" }
                ],
                buses: [{ id: 1 }, { id: 2 }]
            },
            { 
                id: 2, 
                stops: [
                    { id: 1, name: "Центральная" },
                    { id: 3, name: "Южная" }
                ],
                buses: [{ id: 3 }]
            }
        ]), { status: 200 });
    }
    
    if (url.includes('/api/passengers')) {
        return new Response(JSON.stringify([
            { 
                id: 1, 
                timestamp: "2024-01-15T08:30:00",
                bus: { id: 1 },
                stop: { id: 1, name: "Центральная" },
                entered: 15,
                exited: 8
            },
            { 
                id: 2, 
                timestamp: "2024-01-15T09:15:00",
                bus: { id: 2 },
                stop: { id: 2, name: "Северная" },
                entered: 12,
                exited: 5
            }
        ]), { status: 200 });
    }
    
    // Для остальных запросов используем оригинальный fetch
    return originalFetch.apply(this, arguments);
};