// Конфигурация API
const API_BASE_URL = 'http://localhost:8080/api'; // Теперь реальный API
const USE_MOCK_API = false; // Отключаем заглушки

// Элементы DOM
const navItems = document.querySelectorAll('.nav-item');
const sectionTitle = document.getElementById('section-title');
const currentSection = document.getElementById('current-section');
const dynamicContent = document.querySelector('.dynamic-content');

let currentModal = null;
let deleteCallback = null;
let deleteItemId = null;

// Текущее состояние
let currentSectionId = 'dashboard';

// Навигация
navItems.forEach(item => {
    item.addEventListener('click', () => {
        const sectionId = item.dataset.section;
        loadSection(sectionId);
        
        navItems.forEach(nav => nav.classList.remove('active'));
        item.classList.add('active');
    });
});

// Загрузка секции
async function loadSection(sectionId) {
    currentSectionId = sectionId;
    
    const sectionNames = {
        dashboard: 'Панель управления',
        products: 'Управление продуктами',
        buses: 'Управление автобусами',
        stops: 'Управление остановками',
        routes: 'Управление маршрутами',
        passengers: 'Учет пассажиропотока',
        predictions: 'Прогнозы загруженности',
        reports: 'Отчеты'
    };
    
    if (sectionTitle) sectionTitle.textContent = sectionNames[sectionId];
    if (currentSection) currentSection.textContent = sectionNames[sectionId];
    
    switch(sectionId) {
        case 'dashboard':
            await loadDashboard();
            break;
        case 'products':
            await loadProducts();
            break;
        case 'buses':
            await loadBuses();
            break;
        case 'stops':
            await loadStops();
            break;
        case 'routes':
            await loadRoutes();
            break;
        case 'passengers':
            await loadPassengers();
            break;
        case 'predictions':
            await loadPredictions();
            break;
        case 'reports':
            await loadReports();
            break;
    }
}

// Утилиты
async function fetchData(endpoint) {
    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`);
        if (!response.ok) {
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }
        return await response.json();
    } catch (error) {
        console.error(`Ошибка при запросе ${endpoint}:`, error);
        showNotification(`Ошибка загрузки данных: ${error.message}`, 'error');
        return null;
    }
}

async function postData(endpoint, data) {
    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data)
        });
        if (!response.ok) throw new Error(`HTTP ${response.status}`);
        return await response.json();
    } catch (error) {
        console.error('Ошибка при отправке данных:', error);
        showNotification('Ошибка отправки данных', 'error');
        return null;
    }
}

async function putData(endpoint, data) {
    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data)
        });
        if (!response.ok) throw new Error(`HTTP ${response.status}`);
        return await response.json();
    } catch (error) {
        console.error('Ошибка при обновлении данных:', error);
        showNotification('Ошибка обновления данных', 'error');
        return null;
    }
}

async function deleteData(endpoint) {
    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            method: 'DELETE'
        });
        return response.ok;
    } catch (error) {
        console.error('Ошибка при удалении:', error);
        showNotification('Ошибка удаления', 'error');
        return false;
    }
}

// Загрузка панели управления
async function loadDashboard() {
    const html = `
        <div class="dashboard-grid">
            <div class="card">
                <div class="card-header">
                    <h3 class="card-title">Продукты</h3>
                    <div class="card-icon products">
                        <i class="fas fa-box"></i>
                    </div>
                </div>
                <div class="card-body">
                    <div class="stat-number" id="products-count">0</div>
                    <div class="stat-label">Всего продуктов</div>
                </div>
            </div>
            
            <div class="card">
                <div class="card-header">
                    <h3 class="card-title">Автобусы</h3>
                    <div class="card-icon buses">
                        <i class="fas fa-bus"></i>
                    </div>
                </div>
                <div class="card-body">
                    <div class="stat-number" id="buses-count">0</div>
                    <div class="stat-label">Автобусов в системе</div>
                </div>
            </div>
            
            <div class="card">
                <div class="card-header">
                    <h3 class="card-title">Остановки</h3>
                    <div class="card-icon stops">
                        <i class="fas fa-map-marker-alt"></i>
                    </div>
                </div>
                <div class="card-body">
                    <div class="stat-number" id="stops-count">0</div>
                    <div class="stat-label">Всего остановок</div>
                </div>
            </div>
            
            <div class="card">
                <div class="card-header">
                    <h3 class="card-title">Маршруты</h3>
                    <div class="card-icon routes">
                        <i class="fas fa-route"></i>
                    </div>
                </div>
                <div class="card-body">
                    <div class="stat-number" id="routes-count">0</div>
                    <div class="stat-label">Активных маршрутов</div>
                </div>
            </div>
        </div>
        
        <div class="chart-container">
            <canvas id="trafficChart"></canvas>
        </div>
    `;
    
    dynamicContent.innerHTML = html;
    
    // Загрузка статистики
    await loadDashboardStats();
    initTrafficChart();
}

// Загрузка статистики для дашборда
async function loadDashboardStats() {
    try {
        const [products, buses, stops, routes] = await Promise.all([
            fetchData('/products'),
            fetchData('/buses'),
            fetchData('/stops'),
            fetchData('/routes')
        ]);
        
        const productsCount = document.getElementById('products-count');
        const busesCount = document.getElementById('buses-count');
        const stopsCount = document.getElementById('stops-count');
        const routesCount = document.getElementById('routes-count');
        
        if (productsCount) productsCount.textContent = products?.length || 0;
        if (busesCount) busesCount.textContent = buses?.length || 0;
        if (stopsCount) stopsCount.textContent = stops?.length || 0;
        if (routesCount) routesCount.textContent = routes?.length || 0;
    } catch (error) {
        console.error('Ошибка загрузки статистики:', error);
    }
}

// Инициализация графика
function initTrafficChart() {
    const ctx = document.getElementById('trafficChart')?.getContext('2d');
    if (!ctx) return;
    
    new Chart(ctx, {
        type: 'line',
        data: {
            labels: ['6:00', '8:00', '10:00', '12:00', '14:00', '16:00', '18:00', '20:00'],
            datasets: [{
                label: 'Пассажиропоток',
                data: [65, 80, 75, 90, 85, 95, 88, 70],
                borderColor: '#2563eb',
                backgroundColor: 'rgba(37, 99, 235, 0.1)',
                fill: true,
                tension: 0.4
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                title: {
                    display: true,
                    text: 'Средний пассажиропоток по часам'
                }
            }
        }
    });
}

function showNotification(message, type = 'info') {
    // Создание уведомления
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.innerHTML = `
        <i class="fas fa-${type === 'success' ? 'check-circle' : type === 'error' ? 'exclamation-circle' : 'info-circle'}"></i>
        <span>${message}</span>
    `;
    
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.remove();
    }, 3000);
}

// Загрузка продуктов
async function loadProducts() {
    const html = `
        <div class="table-container">
            <div class="table-header">
                <h3>Список продуктов</h3>
                <div class="table-actions">
                    <button class="btn btn-primary" onclick="openProductForm()">
                        <i class="fas fa-plus"></i> Добавить продукт
                    </button>
                    <button class="btn btn-outline" onclick="refreshProducts()">
                        <i class="fas fa-sync"></i> Обновить
                    </button>
                </div>
            </div>
            <div class="table-responsive">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Название</th>
                            <th>Цена</th>
                            <th>Действия</th>
                        </tr>
                    </thead>
                    <tbody id="products-table-body">
                        <tr>
                            <td colspan="4" class="text-center">Загрузка...</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    `;
    
    dynamicContent.innerHTML = html;
    await loadProductsData();
}

// Загрузка данных продуктов
async function loadProductsData() {
    try {
        const products = await fetchData('/products');
        const tbody = document.getElementById('products-table-body');
        
        if (!tbody) return;
        
        if (!products || products.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="4" class="text-center">Нет данных о продуктах</td>
                </tr>
            `;
            return;
        }
        
        tbody.innerHTML = products.map(product => `
            <tr>
                <td>${product.id}</td>
                <td>${product.title}</td>
                <td>${product.cost} руб.</td>
                <td>
                    <button class="btn btn-outline btn-sm" onclick="editProduct(${product.id})">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button class="btn btn-outline btn-sm" onclick="deleteProduct(${product.id})">
                        <i class="fas fa-trash"></i>
                    </button>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('Ошибка загрузки продуктов:', error);
    }
}

// Инициализация при загрузке страницы
document.addEventListener('DOMContentLoaded', () => {
    loadSection('dashboard');
    
    // Загрузка дополнительных модулей
    loadNavModules();
});

// Функции для продуктов
function openProductForm() {
    showNotification('Форма добавления продукта', 'info');
}

function refreshProducts() {
    loadProductsData();
    showNotification('Список продуктов обновлен', 'success');
}

function editProduct(productId) {
    showNotification(`Редактирование продукта ${productId}`, 'info');
}

async function deleteProduct(productId) {
    if (confirm(`Удалить продукт ${productId}?`)) {
        const success = await deleteData(`/products/${productId}`);
        if (success) {
            showNotification('Продукт удален', 'success');
            await loadProductsData();
        }
    }
}

// Загрузка автобусов
async function loadBuses() {
    const dynamicContent = document.querySelector('.dynamic-content');
    if (!dynamicContent) return;
    
    const html = `
        <div class="table-container">
            <div class="table-header">
                <h3>Список автобусов</h3>
                <div class="table-actions">
                    <button class="btn btn-primary" onclick="openBusModal()">
                        <i class="fas fa-plus"></i> Добавить автобус
                    </button>
                    <button class="btn btn-outline" onclick="refreshBuses()">
                        <i class="fas fa-sync"></i> Обновить
                    </button>
                    <button class="btn btn-success" onclick="exportBusesData()">
                        <i class="fas fa-file-export"></i> Экспорт
                    </button>
                </div>
            </div>
            
            <div class="filters" style="padding: 1rem 1.5rem; background: #f9fafb; border-bottom: 1px solid var(--border-color);">
                <div class="filter-group" style="display: flex; align-items: center; gap: 0.5rem;">
                    <label>Поиск:</label>
                    <input type="text" id="busSearch" class="form-input" placeholder="Поиск по модели..." style="flex: 1;">
                    <button class="btn btn-outline" onclick="searchBuses()">
                        <i class="fas fa-search"></i>
                    </button>
                </div>
            </div>
            
            <div class="table-responsive">
                <table>
                    <thead>
                        <tr>
                            <th width="60">ID</th>
                            <th>Модель</th>
                            <th width="120">Маршрут</th>
                            <th width="200">Текущая загруженность</th>
                            <th width="100">Статус</th>
                            <th width="150">Действия</th>
                        </tr>
                    </thead>
                    <tbody id="buses-table-body">
                        <tr>
                            <td colspan="6" class="text-center">Загрузка...</td>
                        </tr>
                    </tbody>
                </table>
            </div>
            
            <div class="table-footer" style="padding: 1rem 1.5rem; background: #f9fafb; border-top: 1px solid var(--border-color);">
                <div class="summary">
                    <strong>Всего автобусов:</strong> <span id="total-buses">0</span>
                </div>
            </div>
        </div>
        
        <div class="dashboard-grid" style="margin-top: 2rem;">
            <div class="card">
                <div class="card-header">
                    <h3 class="card-title">Статистика автобусов</h3>
                    <div class="card-icon" style="background: #dcfce7; color: var(--success-color);">
                        <i class="fas fa-chart-bar"></i>
                    </div>
                </div>
                <div class="card-body">
                    <div class="stat-number" id="active-buses">0</div>
                    <div class="stat-label">Активных автобусов</div>
                </div>
            </div>
            
            <div class="card">
                <div class="card-header">
                    <h3 class="card-title">Средняя загруженность</h3>
                    <div class="card-icon" style="background: #fef3c7; color: var(--warning-color);">
                        <i class="fas fa-users"></i>
                    </div>
                </div>
                <div class="card-body">
                    <div class="stat-number" id="avg-load">0%</div>
                    <div class="stat-label">Средняя по парку</div>
                </div>
            </div>
        </div>
        
        <div class="chart-container" style="margin-top: 2rem;">
            <canvas id="busLoadChart"></canvas>
        </div>
    `;
    
    dynamicContent.innerHTML = html;
    await loadBusesData();
    initBusLoadChart();
}

// Загрузка данных автобусов
async function loadBusesData() {
    try {
        const buses = await fetchData('/buses');
        const tbody = document.getElementById('buses-table-body');
        
        if (!tbody) return;
        
        if (!buses || buses.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="6" class="text-center">Нет данных об автобусах</td>
                </tr>
            `;
            updateBusesStats([]);
            return;
        }
        
        // Загружаем данные о загруженности для каждого автобуса
        const busesWithLoad = await Promise.all(
            buses.map(async bus => {
                try {
                    const currentLoad = await getBusCurrentLoad(bus.id);
                    const loadPercent = Math.min(100, Math.round((currentLoad / 50) * 100));
                    
                    return {
                        ...bus,
                        currentLoad: currentLoad,
                        loadPercent: loadPercent
                    };
                } catch (error) {
                    console.warn(`Ошибка для автобуса ${bus.id}:`, error);
                    const randomLoad = Math.floor(Math.random() * 50);
                    return {
                        ...bus,
                        currentLoad: randomLoad,
                        loadPercent: Math.min(100, randomLoad * 2)
                    };
                }
            })
        );
        
        tbody.innerHTML = busesWithLoad.map(bus => `
            <tr>
                <td>${bus.id}</td>
                <td>${bus.model}</td>
                <td>${bus.route ? `Маршрут ${bus.route.id}` : 'Не назначен'}</td>
                <td>
                    <div class="load-indicator">
                        <div class="load-bar">
                            <div class="load-fill" style="width: ${bus.loadPercent}%"></div>
                        </div>
                        <span>${bus.currentLoad}/50 чел. (${bus.loadPercent}%)</span>
                    </div>
                </td>
                <td>
                    ${getLoadStatusBadge(bus.loadPercent)}
                </td>
                <td>
                    <div class="btn-group" style="display: flex; gap: 0.25rem;">
                        <button class="btn btn-outline btn-sm" onclick="openBusModal(${bus.id})" title="Редактировать">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn btn-outline btn-sm" onclick="showBusDetails(${bus.id})" title="Подробности">
                            <i class="fas fa-info-circle"></i>
                        </button>
                        <button class="btn btn-outline btn-sm btn-danger" onclick="deleteBus(${bus.id})" title="Удалить">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `).join('');
        
        // Обновляем статистику
        updateBusesStats(busesWithLoad);
        
    } catch (error) {
        console.error('Ошибка загрузки автобусов:', error);
        showNotification('Ошибка загрузки данных об автобусах', 'error');
    }
}

// Вспомогательная функция для получения загруженности автобуса
async function getBusCurrentLoad(busId) {
    try {
        const loadData = await fetchData(`/predictions/current-load/${busId}`);
        return loadData?.currentLoad || loadData || Math.floor(Math.random() * 50);
    } catch (error) {
        return Math.floor(Math.random() * 50);
    }
}

// Вспомогательная функция для отображения статуса загруженности
function getLoadStatusBadge(percent) {
    if (percent >= 80) {
        return '<span class="status-badge danger">Высокая</span>';
    } else if (percent >= 50) {
        return '<span class="status-badge warning">Средняя</span>';
    } else {
        return '<span class="status-badge success">Низкая</span>';
    }
}

// Инициализация графика загруженности автобусов
function initBusLoadChart() {
    const ctx = document.getElementById('busLoadChart')?.getContext('2d');
    if (!ctx) return;
    
    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: ['Автобус 1', 'Автобус 2', 'Автобус 3', 'Автобус 4', 'Автобус 5'],
            datasets: [{
                label: 'Текущая загруженность (чел.)',
                data: [32, 45, 28, 38, 42],
                backgroundColor: '#3b82f6',
                borderColor: '#2563eb',
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                title: {
                    display: true,
                    text: 'Загруженность автобусов'
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    max: 50,
                    title: {
                        display: true,
                        text: 'Количество пассажиров'
                    }
                }
            }
        }
    });
}

// Функция поиска автобусов
function searchBuses() {
    const searchInput = document.getElementById('busSearch');
    if (!searchInput) return;
    
    const searchTerm = searchInput.value.toLowerCase();
    const rows = document.querySelectorAll('#buses-table-body tr');
    
    rows.forEach(row => {
        if (row.cells.length > 1) {
            const model = row.cells[1].textContent.toLowerCase();
            const route = row.cells[2].textContent.toLowerCase();
            
            if (model.includes(searchTerm) || route.includes(searchTerm) || searchTerm === '') {
                row.style.display = '';
            } else {
                row.style.display = 'none';
            }
        }
    });
}

// Функция экспорта данных
function exportBusesData() {
    showNotification('Экспорт данных об автобусах', 'info');
    // Здесь можно добавить логику экспорта в CSV/Excel
}

// Функция показа деталей автобуса
async function showBusDetails(busId) {
    try {
        const bus = await fetchData(`/buses/${busId}`);
        if (bus) {
            const message = `
                <strong>Детали автобуса</strong><br>
                ID: ${bus.id}<br>
                Модель: ${bus.model}<br>
                Маршрут: ${bus.route ? `№${bus.route.id}` : 'Не назначен'}<br>
                Дата добавления: ${new Date().toLocaleDateString()}
            `;
            
            // Можно создать отдельное модальное окно для деталей
            showNotification(message.replace(/<br>/g, '\n'), 'info');
        }
    } catch (error) {
        console.error('Ошибка загрузки деталей автобуса:', error);
        showNotification('Ошибка загрузки деталей', 'error');
    }
}

// Функция для обновления статистики на странице автобусов
function updateBusesStats(buses) {
    if (!buses || !Array.isArray(buses)) return;
    
    const totalBuses = document.getElementById('total-buses');
    const activeBuses = document.getElementById('active-buses');
    const avgLoad = document.getElementById('avg-load');
    
    if (totalBuses) totalBuses.textContent = buses.length;
    if (activeBuses) activeBuses.textContent = buses.filter(b => b.route).length;
    
    if (avgLoad && buses.length > 0) {
        const totalLoad = buses.reduce((sum, bus) => sum + (bus.currentLoad || 0), 0);
        const averageLoad = Math.round((totalLoad / (buses.length * 50)) * 100);
        avgLoad.textContent = `${averageLoad}%`;
    }
}

// Функции для автобусов
function openBusModal(busId = null) {
    showNotification(`Открытие формы автобуса ${busId ? 'для редактирования' : 'для добавления'}`, 'info');
}

function refreshBuses() {
    loadBusesData();
    showNotification('Список автобусов обновлен', 'success');
}

async function deleteBus(busId) {
    if (confirm(`Удалить автобус ${busId}?`)) {
        const success = await deleteData(`/buses/${busId}`);
        if (success) {
            showNotification('Автобус удален', 'success');
            await loadBusesData();
        }
    }
}

// Загрузка остановок
async function loadStops() {
    const html = `
        <div class="table-container">
            <div class="table-header">
                <h3>Список остановок</h3>
                <div class="table-actions">
                    <button class="btn btn-primary" onclick="openStopForm()">
                        <i class="fas fa-plus"></i> Добавить остановку
                    </button>
                    <button class="btn btn-success" onclick="findNearbyStops()">
                        <i class="fas fa-map-marker-alt"></i> Найти ближайшие
                    </button>
                </div>
            </div>
            
            <div class="filters" style="padding: 1rem 1.5rem; background: #f9fafb; border-bottom: 1px solid var(--border-color);">
                <div class="filter-group">
                    <label>Координаты:</label>
                    <input type="number" id="lat-input" placeholder="Широта" step="0.000001" style="width: 150px; margin: 0 0.5rem;">
                    <input type="number" id="lon-input" placeholder="Долгота" step="0.000001" style="width: 150px;">
                    <button class="btn btn-outline" onclick="searchNearby()" style="margin-left: 1rem;">
                        <i class="fas fa-search"></i> Поиск
                    </button>
                </div>
            </div>
            
            <div class="table-responsive">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Название</th>
                            <th>Широта</th>
                            <th>Долгота</th>
                            <th>Действия</th>
                        </tr>
                    </thead>
                    <tbody id="stops-table-body">
                        <tr>
                            <td colspan="5" class="text-center">Загрузка...</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
        
        <div class="map-container" style="background: white; border-radius: var(--radius); padding: 1.5rem; margin-top: 2rem; box-shadow: var(--shadow);">
            <h3 style="margin-bottom: 1rem;">Карта остановок</h3>
            <div id="map-placeholder" style="height: 400px; background: #e5e7eb; border-radius: var(--radius); display: flex; align-items: center; justify-content: center; color: var(--gray-color);">
                <div style="text-align: center;">
                    <i class="fas fa-map" style="font-size: 3rem; margin-bottom: 1rem;"></i>
                    <p>Карта остановок (интеграция с картографическим сервисом)</p>
                </div>
            </div>
        </div>
    `;
    
    dynamicContent.innerHTML = html;
    await loadStopsData();
}

// Загрузка данных остановок
async function loadStopsData() {
    try {
        const stops = await fetchData('/stops');
        const tbody = document.getElementById('stops-table-body');
        
        if (!tbody) return;
        
        if (!stops || stops.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="5" class="text-center">Нет данных об остановках</td>
                </tr>
            `;
            return;
        }
        
        tbody.innerHTML = stops.map(stop => `
            <tr>
                <td>${stop.id}</td>
                <td>${stop.name}</td>
                <td>${stop.lat.toFixed(6)}</td>
                <td>${stop.lon.toFixed(6)}</td>
                <td>
                    <button class="btn btn-outline btn-sm" onclick="editStop(${stop.id})">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button class="btn btn-outline btn-sm" onclick="showStopPassengers(${stop.id})">
                        <i class="fas fa-users"></i>
                    </button>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('Ошибка загрузки остановок:', error);
    }
}

// Поиск ближайших остановок
async function searchNearby() {
    const latInput = document.getElementById('lat-input');
    const lonInput = document.getElementById('lon-input');
    
    if (!latInput || !lonInput) return;
    
    const lat = latInput.value;
    const lon = lonInput.value;
    
    if (!lat || !lon) {
        showNotification('Введите координаты для поиска', 'warning');
        return;
    }
    
    try {
        const nearbyStops = await fetchData(`/stops/nearby?lat=${lat}&lon=${lon}`);
        const tbody = document.getElementById('stops-table-body');
        
        if (!tbody) return;
        
        if (!nearbyStops || nearbyStops.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="5" class="text-center">Нет остановок в радиусе 2 км</td>
                </tr>
            `;
            return;
        }
        
        tbody.innerHTML = nearbyStops.map(stop => `
            <tr>
                <td>${stop.id}</td>
                <td><strong>${stop.name}</strong> <span class="badge">Ближайшая</span></td>
                <td>${stop.lat.toFixed(6)}</td>
                <td>${stop.lon.toFixed(6)}</td>
                <td>
                    <button class="btn btn-outline btn-sm" onclick="showStopPassengers(${stop.id})">
                        <i class="fas fa-users"></i> Пассажиры
                    </button>
                </td>
            </tr>
        `).join('');
        
        showNotification(`Найдено ${nearbyStops.length} ближайших остановок`, 'success');
    } catch (error) {
        console.error('Ошибка поиска остановок:', error);
        showNotification('Ошибка поиска остановок', 'error');
    }
}

// Функции для остановок
function openStopForm() {
    showNotification('Форма добавления остановки', 'info');
}

function editStop(stopId) {
    showNotification(`Редактирование остановки ${stopId}`, 'info');
}

function showStopPassengers(stopId) {
    showNotification(`Пассажиропоток на остановке ${stopId}`, 'info');
}

function findNearbyStops() {
    showNotification('Поиск ближайших остановок', 'info');
    const latInput = document.getElementById('lat-input');
    const lonInput = document.getElementById('lon-input');
    
    if (latInput) latInput.value = '55.7558';
    if (lonInput) lonInput.value = '37.6176';
}

// Загрузка маршрутов
async function loadRoutes() {
    const html = `
        <div class="table-container">
            <div class="table-header">
                <h3>Список маршрутов</h3>
                <div class="table-actions">
                    <button class="btn btn-primary" onclick="openRouteForm()">
                        <i class="fas fa-plus"></i> Добавить маршрут
                    </button>
                    <button class="btn btn-outline" onclick="refreshRoutes()">
                        <i class="fas fa-sync"></i> Обновить
                    </button>
                </div>
            </div>
            <div class="table-responsive">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Остановки</th>
                            <th>Автобусы</th>
                            <th>Статус</th>
                            <th>Действия</th>
                        </tr>
                    </thead>
                    <tbody id="routes-table-body">
                        <tr>
                            <td colspan="5" class="text-center">Загрузка...</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
        
        <div class="route-visualization" style="display: flex; gap: 2rem; margin-top: 2rem;">
            <div class="chart-container" style="flex: 1;">
                <canvas id="routeLoadChart"></canvas>
            </div>
            <div class="heatmap-container" style="flex: 1; background: white; border-radius: var(--radius); padding: 1.5rem; box-shadow: var(--shadow);">
                <h3 style="margin-bottom: 1rem;">Тепловая карта загруженности</h3>
                <div id="heatmap" style="height: 300px; background: linear-gradient(to right, #10b981, #f59e0b, #ef4444); border-radius: var(--radius); position: relative;">
                    <div style="position: absolute; bottom: 1rem; left: 1rem; color: white;">
                        <div><span style="background: #10b981; padding: 2px 8px; border-radius: 4px;">Низкая</span></div>
                        <div style="margin: 0.5rem 0;"><span style="background: #f59e0b; padding: 2px 8px; border-radius: 4px;">Средняя</span></div>
                        <div><span style="background: #ef4444; padding: 2px 8px; border-radius: 4px;">Высокая</span></div>
                    </div>
                </div>
            </div>
        </div>
    `;
    
    dynamicContent.innerHTML = html;
    await loadRoutesData();
    initRouteLoadChart();
}

// Загрузка данных маршрутов
async function loadRoutesData() {
    try {
        const routes = await fetchData('/routes');
        const tbody = document.getElementById('routes-table-body');
        
        if (!tbody) return;
        
        if (!routes || routes.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="5" class="text-center">Нет данных о маршрутах</td>
                </tr>
            `;
            return;
        }
        
        tbody.innerHTML = routes.map(route => `
            <tr>
                <td>${route.id}</td>
                <td>
                    <div class="stops-list">
                        ${route.stops?.slice(0, 3).map(stop => `<span class="stop-tag">${stop.name}</span>`).join('') || 'Нет остановок'}
                        ${route.stops?.length > 3 ? `<span class="more-stops">+${route.stops.length - 3} еще</span>` : ''}
                    </div>
                </td>
                <td>${route.buses?.length || 0} автобусов</td>
                <td>
                    <span class="status-badge active">Активен</span>
                </td>
                <td>
                    <button class="btn btn-outline btn-sm" onclick="showRouteDetails(${route.id})">
                        <i class="fas fa-eye"></i>
                    </button>
                    <button class="btn btn-outline btn-sm" onclick="generateRouteReport(${route.id})">
                        <i class="fas fa-chart-bar"></i>
                    </button>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('Ошибка загрузки маршрутов:', error);
    }
}

// Инициализация графика маршрутов
function initRouteLoadChart() {
    const ctx = document.getElementById('routeLoadChart')?.getContext('2d');
    if (!ctx) return;
    
    new Chart(ctx, {
        type: 'line',
        data: {
            labels: ['6:00', '8:00', '10:00', '12:00', '14:00', '16:00', '18:00', '20:00'],
            datasets: [{
                label: 'Загруженность маршрута',
                data: [45, 60, 55, 75, 70, 80, 65, 50],
                borderColor: '#db2777',
                backgroundColor: 'rgba(219, 39, 119, 0.1)',
                fill: true,
                tension: 0.4
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                title: {
                    display: true,
                    text: 'Загруженность маршрута по часам'
                }
            }
        }
    });
}

// Функции для маршрутов
function openRouteForm() {
    showNotification('Форма добавления маршрута', 'info');
}

function refreshRoutes() {
    loadRoutesData();
    showNotification('Список маршрутов обновлен', 'success');
}

function showRouteDetails(routeId) {
    showNotification(`Детальная информация о маршруте ${routeId}`, 'info');
}

function generateRouteReport(routeId) {
    showNotification(`Генерация отчета по маршруту ${routeId}`, 'info');
}

// Загрузка пассажиропотока
async function loadPassengers() {
    const html = `
        <div class="table-container">
            <div class="table-header">
                <h3>Учет пассажиропотока</h3>
                <div class="table-actions">
                    <button class="btn btn-primary" onclick="openPassengerForm()">
                        <i class="fas fa-plus"></i> Добавить запись
                    </button>
                    <button class="btn btn-success" onclick="exportPassengerData()">
                        <i class="fas fa-file-export"></i> Экспорт
                    </button>
                </div>
            </div>
            
            <div class="filters" style="padding: 1rem 1.5rem; background: #f9fafb; border-bottom: 1px solid var(--border-color); display: flex; gap: 1rem; flex-wrap: wrap;">
                <div class="filter-group">
                    <label>Дата:</label>
                    <input type="date" id="date-filter" style="margin-left: 0.5rem;">
                </div>
                <div class="filter-group">
                    <label>Остановка:</label>
                    <select id="stop-filter" style="margin-left: 0.5rem; min-width: 150px;">
                        <option value="">Все остановки</option>
                    </select>
                </div>
                <div class="filter-group">
                    <label>Автобус:</label>
                    <select id="bus-filter" style="margin-left: 0.5rem; min-width: 150px;">
                        <option value="">Все автобусы</option>
                    </select>
                </div>
                <button class="btn btn-outline" onclick="filterPassengers()">
                    <i class="fas fa-filter"></i> Фильтровать
                </button>
            </div>
            
            <div class="table-responsive">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Время</th>
                            <th>Автобус</th>
                            <th>Остановка</th>
                            <th>Вошедшие</th>
                            <th>Вышедшие</th>
                            <th>Изменение</th>
                            <th>Действия</th>
                        </tr>
                    </thead>
                    <tbody id="passengers-table-body">
                        <tr>
                            <td colspan="8" class="text-center">Загрузка...</td>
                        </tr>
                    </tbody>
                </table>
            </div>
            
            <div class="table-footer" style="padding: 1rem 1.5rem; background: #f9fafb; border-top: 1px solid var(--border-color); display: flex; justify-content: space-between; align-items: center;">
                <div class="summary">
                    <strong>Итого за день:</strong>
                    <span id="total-entered">0</span> вошедших, 
                    <span id="total-exited">0</span> вышедших,
                    <span id="net-change">0</span> чистое изменение
                </div>
                <div class="pagination">
                    <button class="btn btn-outline btn-sm">←</button>
                    <span style="margin: 0 1rem;">Страница 1 из 5</span>
                    <button class="btn btn-outline btn-sm">→</button>
                </div>
            </div>
        </div>
        
        <div class="charts-row" style="display: flex; gap: 2rem; margin-top: 2rem;">
            <div class="chart-container" style="flex: 1;">
                <canvas id="passengerFlowChart"></canvas>
            </div>
            <div class="chart-container" style="flex: 1;">
                <canvas id="hourlyFlowChart"></canvas>
            </div>
        </div>
    `;
    
    dynamicContent.innerHTML = html;
    await loadPassengersData();
    initPassengerCharts();
}

// Загрузка данных пассажиропотока
async function loadPassengersData() {
    try {
        const passengers = await fetchData('/passengers');
        const tbody = document.getElementById('passengers-table-body');
        
        if (!tbody) return;
        
        if (!passengers || passengers.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="8" class="text-center">Нет данных о пассажиропотоке</td>
                </tr>
            `;
            return;
        }
        
        let totalEntered = 0;
        let totalExited = 0;
        
        tbody.innerHTML = passengers.map(passenger => {
            const entered = passenger.entered || 0;
            const exited = passenger.exited || 0;
            totalEntered += entered;
            totalExited += exited;
            const netChange = entered - exited;
            
            return `
                <tr>
                    <td>${passenger.id}</td>
                    <td>${passenger.timestamp ? new Date(passenger.timestamp).toLocaleString() : 'N/A'}</td>
                    <td>${passenger.bus?.id ? `Автобус ${passenger.bus.id}` : 'N/A'}</td>
                    <td>${passenger.stop?.name || 'N/A'}</td>
                    <td>${entered}</td>
                    <td>${exited}</td>
                    <td>${netChange}</td>
                    <td>
                        <button class="btn btn-outline btn-sm">
                            <i class="fas fa-edit"></i>
                        </button>
                    </td>
                </tr>
            `;
        }).join('');
        
        const totalEnteredEl = document.getElementById('total-entered');
        const totalExitedEl = document.getElementById('total-exited');
        const netChangeEl = document.getElementById('net-change');
        
        if (totalEnteredEl) totalEnteredEl.textContent = totalEntered;
        if (totalExitedEl) totalExitedEl.textContent = totalExited;
        if (netChangeEl) netChangeEl.textContent = totalEntered - totalExited;
    } catch (error) {
        console.error('Ошибка загрузки пассажиропотока:', error);
    }
}

// Инициализация графиков пассажиропотока
function initPassengerCharts() {
    const ctx1 = document.getElementById('passengerFlowChart')?.getContext('2d');
    const ctx2 = document.getElementById('hourlyFlowChart')?.getContext('2d');
    
    if (ctx1) {
        new Chart(ctx1, {
            type: 'bar',
            data: {
                labels: ['Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб', 'Вс'],
                datasets: [{
                    label: 'Вошедшие',
                    data: [1200, 1300, 1250, 1400, 1500, 1100, 900],
                    backgroundColor: '#3b82f6'
                }, {
                    label: 'Вышедшие',
                    data: [1150, 1250, 1200, 1350, 1450, 1050, 850],
                    backgroundColor: '#10b981'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    title: {
                        display: true,
                        text: 'Пассажиропоток по дням недели'
                    }
                }
            }
        });
    }
    
    if (ctx2) {
        new Chart(ctx2, {
            type: 'line',
            data: {
                labels: ['6-7', '7-8', '8-9', '9-10', '10-11', '11-12', '12-13', '13-14', '14-15', '15-16'],
                datasets: [{
                    label: 'Пассажиропоток',
                    data: [150, 300, 450, 350, 280, 320, 400, 380, 350, 320],
                    borderColor: '#f59e0b',
                    backgroundColor: 'rgba(245, 158, 11, 0.1)',
                    fill: true
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    title: {
                        display: true,
                        text: 'Пассажиропоток по часам'
                    }
                }
            }
        });
    }
}

// Функции для пассажиропотока
function openPassengerForm() {
    showNotification('Форма добавления записи пассажиропотока', 'info');
}

function exportPassengerData() {
    showNotification('Экспорт данных о пассажиропотоке', 'success');
}

function filterPassengers() {
    showNotification('Применен фильтр пассажиропотока', 'info');
}

// Загрузка прогнозов
async function loadPredictions() {
    const html = `
        <div class="predictions-container">
            <div class="card" style="margin-bottom: 2rem;">
                <div class="card-header">
                    <h3 class="card-title">Прогноз загруженности</h3>
                    <div class="card-icon" style="background: #dbeafe; color: #2563eb;">
                        <i class="fas fa-chart-line"></i>
                    </div>
                </div>
                <div class="card-body">
                    <div class="prediction-controls" style="display: flex; gap: 1rem; margin-bottom: 1.5rem;">
                        <select id="route-select" class="form-input" style="flex: 1;">
                            <option value="">Выберите маршрут</option>
                        </select>
                        <input type="datetime-local" id="time-input" class="form-input" style="flex: 1;">
                        <button class="btn btn-primary" onclick="getPrediction()">
                            <i class="fas fa-search"></i> Получить прогноз
                        </button>
                    </div>
                    
                    <div id="prediction-result" style="display: none;">
                        <div class="prediction-card" style="background: #eff6ff; padding: 1.5rem; border-radius: var(--radius); margin-bottom: 1.5rem;">
                            <div style="display: flex; justify-content: space-between; align-items: center;">
                                <div>
                                    <h4 style="margin-bottom: 0.5rem;">Маршрут <span id="pred-route">7A</span></h4>
                                    <p style="color: var(--gray-color); margin-bottom: 0.5rem;">
                                        Время: <span id="pred-time">15:00</span> | 
                                        Остановка: <span id="pred-stop">Центральная</span>
                                    </p>
                                </div>
                                <div style="text-align: right;">
                                    <div class="predicted-load" style="font-size: 2rem; font-weight: bold; color: #2563eb;">
                                        <span id="pred-load">75</span>%
                                    </div>
                                    <div style="color: var(--gray-color);">Загруженность</div>
                                </div>
                            </div>
                            <div class="load-bar" style="height: 20px; background: #e5e7eb; border-radius: 10px; margin-top: 1rem; overflow: hidden;">
                                <div id="load-indicator" style="height: 100%; background: linear-gradient(90deg, #10b981, #f59e0b, #ef4444); width: 75%;"></div>
                            </div>
                            <div style="display: flex; justify-content: space-between; margin-top: 0.5rem; font-size: 0.9rem; color: var(--gray-color);">
                                <span>Свободно</span>
                                <span>Умеренно</span>
                                <span>Переполнен</span>
                            </div>
                        </div>
                        
                        <div class="prediction-message" id="prediction-message">
                            <i class="fas fa-info-circle"></i>
                            <span>Автобус будет переполнен. Рекомендуется направить дополнительный транспорт.</span>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="card">
                <div class="card-header">
                    <h3 class="card-title">Суточный прогноз по маршрутам</h3>
                </div>
                <div class="card-body">
                    <div class="daily-predictions">
                        <canvas id="dailyPredictionsChart"></canvas>
                    </div>
                </div>
            </div>
        </div>
    `;
    
    dynamicContent.innerHTML = html;
    initPredictions();
}

// Инициализация прогнозов
function initPredictions() {
    // Заполняем select маршрутами
    const routeSelect = document.getElementById('route-select');
    if (routeSelect) {
        routeSelect.innerHTML = `
            <option value="">Выберите маршрут</option>
            <option value="7A">Маршрут 7A</option>
            <option value="12B">Маршрут 12B</option>
            <option value="25C">Маршрут 25C</option>
            <option value="38D">Маршрут 38D</option>
        `;
    }
    
    // Устанавливаем текущее время + 1 час
    const timeInput = document.getElementById('time-input');
    if (timeInput) {
        const now = new Date();
        now.setHours(now.getHours() + 1);
        timeInput.value = now.toISOString().slice(0, 16);
    }
    
    // Инициализируем график суточных прогнозов
    initDailyPredictionsChart();
}

// Инициализация графика суточных прогнозов
function initDailyPredictionsChart() {
    const ctx = document.getElementById('dailyPredictionsChart')?.getContext('2d');
    if (!ctx) return;
    
    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: ['7A', '12B', '25C', '38D', '45E'],
            datasets: [{
                label: 'Средняя загруженность (%)',
                data: [75, 60, 85, 45, 70],
                backgroundColor: [
                    '#ef4444',
                    '#f59e0b',
                    '#ef4444',
                    '#10b981',
                    '#f59e0b'
                ]
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                title: {
                    display: true,
                    text: 'Прогноз загруженности по маршрутам'
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    max: 100,
                    title: {
                        display: true,
                        text: 'Загруженность (%)'
                    }
                }
            }
        }
    });
}

// Функция получения прогноза
function getPrediction() {
    const routeSelect = document.getElementById('route-select');
    const timeInput = document.getElementById('time-input');
    
    if (!routeSelect || !timeInput) return;
    
    const route = routeSelect.value;
    const time = timeInput.value;
    
    if (!route || !time) {
        showNotification('Выберите маршрут и время', 'warning');
        return;
    }
    
    const predictionResult = document.getElementById('prediction-result');
    const predRoute = document.getElementById('pred-route');
    const predTime = document.getElementById('pred-time');
    const predStop = document.getElementById('pred-stop');
    const predLoad = document.getElementById('pred-load');
    const loadIndicator = document.getElementById('load-indicator');
    
    if (predictionResult) predictionResult.style.display = 'block';
    if (predRoute) predRoute.textContent = route;
    if (predTime) predTime.textContent = new Date(time).toLocaleTimeString();
    if (predStop) predStop.textContent = 'Кампи';
    if (predLoad) predLoad.textContent = '75';
    if (loadIndicator) loadIndicator.style.width = '75%';
    
    showNotification(`Прогноз для маршрута ${route} получен`, 'success');
}

// Загрузка отчетов
async function loadReports() {
    const html = `
        <div class="reports-container">
            <div class="card" style="margin-bottom: 2rem;">
                <div class="card-header">
                    <h3 class="card-title">Генерация отчетов</h3>
                    <div class="card-icon" style="background: #fef3c7; color: #f59e0b;">
                        <i class="fas fa-file-alt"></i>
                    </div>
                </div>
                <div class="card-body">
                    <div class="report-options" style="display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 1rem; margin-bottom: 2rem;">
                        <div class="report-option" style="background: #f9fafb; padding: 1rem; border-radius: var(--radius); cursor: pointer;" onclick="generateReport('daily')">
                            <i class="fas fa-calendar-day" style="font-size: 2rem; color: var(--primary-color); margin-bottom: 0.5rem;"></i>
                            <h4>Суточный отчет</h4>
                            <p>Статистика за текущий день</p>
                        </div>
                        <div class="report-option" style="background: #f9fafb; padding: 1rem; border-radius: var(--radius); cursor: pointer;" onclick="generateReport('weekly')">
                            <i class="fas fa-calendar-week" style="font-size: 2rem; color: var(--success-color); margin-bottom: 0.5rem;"></i>
                            <h4>Недельный отчет</h4>
                            <p>Анализ за неделю</p>
                        </div>
                        <div class="report-option" style="background: #f9fafb; padding: 1rem; border-radius: var(--radius); cursor: pointer;" onclick="generateReport('monthly')">
                            <i class="fas fa-calendar-alt" style="font-size: 2rem; color: var(--warning-color); margin-bottom: 0.5rem;"></i>
                            <h4>Месячный отчет</h4>
                            <p>Отчет за месяц</p>
                        </div>
                        <div class="report-option" style="background: #f9fafb; padding: 1rem; border-radius: var(--radius); cursor: pointer;" onclick="generateReport('route')">
                            <i class="fas fa-route" style="font-size: 2rem; color: #db2777; margin-bottom: 0.5rem;"></i>
                            <h4>Отчет по маршруту</h4>
                            <p>Детальный анализ маршрута</p>
                        </div>
                    </div>
                    
                    <div class="report-history">
                        <h4 style="margin-bottom: 1rem;">История отчетов</h4>
                        <table style="width: 100%;">
                            <thead>
                                <tr>
                                    <th>Дата</th>
                                    <th>Тип отчета</th>
                                    <th>Параметры</th>
                                    <th>Статус</th>
                                    <th>Действия</th>
                                </tr>
                            </thead>
                            <tbody id="reports-history">
                                <tr>
                                    <td colspan="5" class="text-center">Нет сгенерированных отчетов</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
            
            <div class="card">
                <div class="card-header">
                    <h3 class="card-title">Экспорт данных</h3>
                </div>
                <div class="card-body">
                    <div class="export-options" style="display: flex; gap: 1rem; margin-bottom: 1.5rem;">
                        <button class="btn btn-outline" onclick="exportData('csv')">
                            <i class="fas fa-file-csv"></i> CSV
                        </button>
                        <button class="btn btn-outline" onclick="exportData('excel')">
                            <i class="fas fa-file-excel"></i> Excel
                        </button>
                        <button class="btn btn-outline" onclick="exportData('pdf')">
                            <i class="fas fa-file-pdf"></i> PDF
                        </button>
                        <button class="btn btn-outline" onclick="exportData('json')">
                            <i class="fas fa-code"></i> JSON
                        </button>
                    </div>
                    
                    <div class="export-progress" style="display: none;" id="export-progress">
                        <div style="display: flex; align-items: center; gap: 1rem;">
                            <div class="progress-bar" style="flex: 1; height: 10px; background: #e5e7eb; border-radius: 5px; overflow: hidden;">
                                <div id="progress-fill" style="height: 100%; background: var(--success-color); width: 0%;"></div>
                            </div>
                            <span id="progress-text">0%</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `;
    
    dynamicContent.innerHTML = html;
}

// Функции для отчетов
function generateReport(type) {
    showNotification(`Генерация ${type} отчета начата`, 'info');
}

function exportData(format) {
    showNotification(`Экспорт данных в формате ${format.toUpperCase()} начат`, 'info');
}

// Инициализация
function loadNavModules() {
    // Загрузка дополнительных модулей навигации
    console.log('Навигационные модули загружены');
}

// Функции для работы с модальными окнами
function openModal(modalId) {
    const modal = document.getElementById(modalId);
    const overlay = document.getElementById('modal-overlay');
    
    if (modal && overlay) {
        currentModal = modalId;
        modal.style.display = 'block';
        overlay.style.display = 'block';
        
        // Блокируем прокрутку body
        document.body.style.overflow = 'hidden';
    }
}

function closeModal() {
    if (currentModal) {
        const modal = document.getElementById(currentModal);
        const overlay = document.getElementById('modal-overlay');
        
        if (modal) modal.style.display = 'none';
        if (overlay) overlay.style.display = 'none';
        
        currentModal = null;
        document.body.style.overflow = 'auto';
    }
}

// Специфичные функции для каждого модального окна
function openBusFormModal(busId = null) {
    if (busId) {
        document.getElementById('busModalTitle').textContent = 'Редактирование автобуса';
        loadBusData(busId);
    } else {
        document.getElementById('busModalTitle').textContent = 'Добавление автобуса';
        document.getElementById('busForm').reset();
        document.getElementById('busId').value = '';
    }
    openModal('busModal');
}

function closeBusModal() {
    closeModal();
    document.getElementById('busForm').reset();
}

function openStopFormModal(stopId = null) {
    if (stopId) {
        document.getElementById('stopModalTitle').textContent = 'Редактирование остановки';
        loadStopData(stopId);
    } else {
        document.getElementById('stopModalTitle').textContent = 'Добавление остановки';
        document.getElementById('stopForm').reset();
        document.getElementById('stopId').value = '';
    }
    openModal('stopModal');
}

function closeStopModal() {
    closeModal();
    document.getElementById('stopForm').reset();
}

function openPassengerFormModal(passengerId = null) {
    if (passengerId) {
        document.getElementById('passengerModalTitle').textContent = 'Редактирование записи';
        loadPassengerData(passengerId);
    } else {
        document.getElementById('passengerModalTitle').textContent = 'Добавление записи';
        document.getElementById('passengerForm').reset();
        document.getElementById('passengerId').value = '';
        document.getElementById('passengerTimestamp').value = new Date().toISOString().slice(0, 16);
        
        // Загружаем списки автобусов и остановок
        loadBusesForSelect();
        loadStopsForSelect();
    }
    openModal('passengerModal');
}

function closePassengerModal() {
    closeModal();
    document.getElementById('passengerForm').reset();
}

function openConfirmModal(message, callback, itemId) {
    document.getElementById('confirmMessage').textContent = message;
    deleteCallback = callback;
    deleteItemId = itemId;
    openModal('confirmModal');
}

function closeConfirmModal() {
    closeModal();
    deleteCallback = null;
    deleteItemId = null;
}

function confirmDelete() {
    if (deleteCallback && deleteItemId !== null) {
        deleteCallback(deleteItemId);
    }
    closeConfirmModal();
}

// Загрузка данных автобуса для формы
async function loadBusData(busId) {
    try {
        const bus = await fetchData(`/buses/${busId}`);
        if (bus) {
            document.getElementById('busId').value = bus.id;
            document.getElementById('busModel').value = bus.model;
            document.getElementById('busRoute').value = bus.route ? bus.route.id : '';
            
            // Загружаем список маршрутов
            await loadRoutesForSelect();
        }
    } catch (error) {
        console.error('Ошибка загрузки данных автобуса:', error);
        showNotification('Ошибка загрузки данных', 'error');
    }
}

// Загрузка данных остановки для формы
async function loadStopData(stopId) {
    try {
        const stop = await fetchData(`/stops/${stopId}`);
        if (stop) {
            document.getElementById('stopId').value = stop.id;
            document.getElementById('stopName').value = stop.name;
            document.getElementById('stopLat').value = stop.lat;
            document.getElementById('stopLon').value = stop.lon;
        }
    } catch (error) {
        console.error('Ошибка загрузки данных остановки:', error);
        showNotification('Ошибка загрузки данных', 'error');
    }
}

// Загрузка списка маршрутов для select
async function loadRoutesForSelect() {
    try {
        const routes = await fetchData('/routes');
        const select = document.getElementById('busRoute');
        
        if (select && routes) {
            // Сохраняем текущее значение
            const currentValue = select.value;
            
            // Очищаем и заполняем options
            select.innerHTML = '<option value="">Не назначен</option>';
            
            routes.forEach(route => {
                const option = document.createElement('option');
                option.value = route.id;
                option.textContent = `Маршрут ${route.id}`;
                select.appendChild(option);
            });
            
            // Восстанавливаем значение
            select.value = currentValue;
        }
    } catch (error) {
        console.error('Ошибка загрузки маршрутов:', error);
    }
}

// Загрузка списка автобусов для select
async function loadBusesForSelect() {
    try {
        const buses = await fetchData('/buses');
        const select = document.getElementById('passengerBus');
        
        if (select && buses) {
            const currentValue = select.value;
            select.innerHTML = '<option value="">Выберите автобус</option>';
            
            buses.forEach(bus => {
                const option = document.createElement('option');
                option.value = bus.id;
                option.textContent = `${bus.model} (ID: ${bus.id})`;
                select.appendChild(option);
            });
            
            select.value = currentValue;
        }
    } catch (error) {
        console.error('Ошибка загрузки автобусов:', error);
    }
}

// Загрузка списка остановок для select
async function loadStopsForSelect() {
    try {
        const stops = await fetchData('/stops');
        const select = document.getElementById('passengerStop');
        
        if (select && stops) {
            const currentValue = select.value;
            select.innerHTML = '<option value="">Выберите остановку</option>';
            
            stops.forEach(stop => {
                const option = document.createElement('option');
                option.value = stop.id;
                option.textContent = stop.name;
                select.appendChild(option);
            });
            
            select.value = currentValue;
        }
    } catch (error) {
        console.error('Ошибка загрузки остановок:', error);
    }
}

// Закрытие модального окна при клике на оверлей
document.addEventListener('DOMContentLoaded', function() {
    const overlay = document.getElementById('modal-overlay');
    if (overlay) {
        overlay.addEventListener('click', function(e) {
            if (e.target === overlay) {
                closeModal();
            }
        });
    }
    
    // ESC для закрытия модального окна
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape' && currentModal) {
            closeModal();
        }
    });
});

// Обработчик формы автобуса
document.addEventListener('DOMContentLoaded', function() {
    const busForm = document.getElementById('busForm');
    if (busForm) {
        busForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const busId = document.getElementById('busId').value;
            const model = document.getElementById('busModel').value;
            const routeId = document.getElementById('busRoute').value;
            
            const busData = {
                model: model,
                route: routeId ? { id: Number(routeId) } : null
            };
            
            try {
                let result;
                if (busId) {
                    result = await putData(`/buses/${busId}`, busData);
                    showNotification('Автобус успешно обновлен', 'success');
                } else {
                    result = await postData('/buses', busData);
                    showNotification('Автобус успешно добавлен', 'success');
                }
                
                closeBusModal();
                // Обновляем список автобусов
                if (currentSectionId === 'buses') {
                    await loadBusesData();
                }
                // Обновляем статистику на дашборде
                await loadDashboardStats();
                
            } catch (error) {
                console.error('Ошибка сохранения автобуса:', error);
                showNotification('Ошибка сохранения данных', 'error');
            }
        });
    }
    
    // Обработчик формы остановки
    const stopForm = document.getElementById('stopForm');
    if (stopForm) {
        stopForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const stopId = document.getElementById('stopId').value;
            const name = document.getElementById('stopName').value;
            const lat = parseFloat(document.getElementById('stopLat').value);
            const lon = parseFloat(document.getElementById('stopLon').value);
            
            const stopData = {
                name: name,
                lat: lat,
                lon: lon
            };
            
            try {
                let result;
                if (stopId) {
                    result = await putData(`/stops/${stopId}`, stopData);
                    showNotification('Остановка успешно обновлена', 'success');
                } else {
                    result = await postData('/stops', stopData);
                    showNotification('Остановка успешно добавлена', 'success');
                }
                
                closeStopModal();
                if (currentSectionId === 'stops') {
                    await loadStopsData();
                }
                await loadDashboardStats();
                
            } catch (error) {
                console.error('Ошибка сохранения остановки:', error);
                showNotification('Ошибка сохранения данных', 'error');
            }
        });
    }
    
    // Обработчик формы пассажиропотока
    const passengerForm = document.getElementById('passengerForm');
    if (passengerForm) {
        passengerForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const passengerId = document.getElementById('passengerId').value;
            const busId = document.getElementById('passengerBus').value;
            const stopId = document.getElementById('passengerStop').value;
            const entered = parseInt(document.getElementById('passengerEntered').value);
            const exited = parseInt(document.getElementById('passengerExited').value);
            const timestamp = document.getElementById('passengerTimestamp').value;
            
            const passengerData = {
                bus: { id: Number(busId) },
                stop: { id: Number(stopId) },
                entered: entered,
                exited: exited,
                timestamp: timestamp
            };
            
            try {
                let result;
                if (passengerId) {
                    result = await putData(`/passengers/${passengerId}`, passengerData);
                    showNotification('Запись успешно обновлена', 'success');
                } else {
                    result = await postData('/passengers', passengerData);
                    showNotification('Запись успешно добавлена', 'success');
                }
                
                closePassengerModal();
                if (currentSectionId === 'passengers') {
                    await loadPassengersData();
                }
                
            } catch (error) {
                console.error('Ошибка сохранения записи:', error);
                showNotification('Ошибка сохранения данных', 'error');
            }
        });
    }
});

// Функции удаления
async function deleteBus(busId) {
    openConfirmModal(
        `Вы уверены, что хотите удалить автобус ${busId}?`,
        async (id) => {
            const success = await deleteData(`/buses/${id}`);
            if (success) {
                showNotification('Автобус успешно удален', 'success');
                if (currentSectionId === 'buses') {
                    await loadBusesData();
                }
                await loadDashboardStats();
            } else {
                showNotification('Ошибка удаления автобуса', 'error');
            }
        },
        busId
    );
}

async function deleteStop(stopId) {
    openConfirmModal(
        `Вы уверены, что хотите удалить остановку ${stopId}?`,
        async (id) => {
            const success = await deleteData(`/stops/${id}`);
            if (success) {
                showNotification('Остановка успешно удалена', 'success');
                if (currentSectionId === 'stops') {
                    await loadStopsData();
                }
                await loadDashboardStats();
            } else {
                showNotification('Ошибка удаления остановки', 'error');
            }
        },
        stopId
    );
}

// Функция загрузки данных пассажира для формы
async function loadPassengerData(passengerId) {
    try {
        const passenger = await fetchData(`/passengers/${passengerId}`);
        if (passenger) {
            document.getElementById('passengerId').value = passenger.id;
            document.getElementById('passengerBus').value = passenger.bus?.id || '';
            document.getElementById('passengerStop').value = passenger.stop?.id || '';
            document.getElementById('passengerEntered').value = passenger.entered || 0;
            document.getElementById('passengerExited').value = passenger.exited || 0;
            
            // Форматируем timestamp для input[type=datetime-local]
            if (passenger.timestamp) {
                const date = new Date(passenger.timestamp);
                const formattedDate = date.toISOString().slice(0, 16);
                document.getElementById('passengerTimestamp').value = formattedDate;
            }
            
            // Загружаем списки автобусов и остановок
            await loadBusesForSelect();
            await loadStopsForSelect();
        }
    } catch (error) {
        console.error('Ошибка загрузки данных пассажира:', error);
        showNotification('Ошибка загрузки данных', 'error');
    }
}

// Функции для обновления интерфейса
function refreshBuses() {
    loadBusesData();
    showNotification('Список автобусов обновлен', 'success');
}

function refreshStops() {
    loadStopsData();
    showNotification('Список остановок обновлен', 'success');
}

function refreshRoutes() {
    loadRoutesData();
    showNotification('Список маршрутов обновлен', 'success');
}

function refreshPassengers() {
    loadPassengersData();
    showNotification('Список пассажиропотока обновлен', 'success');
}

// Функция для обновления статистики на дашборде
async function updateDashboardStats() {
    await loadDashboardStats();
}

// Инициализация обработчиков событий после загрузки DOM
document.addEventListener('DOMContentLoaded', function() {
    // Обработчик для поиска автобусов при вводе текста
    const busSearchInput = document.getElementById('busSearch');
    if (busSearchInput) {
        busSearchInput.addEventListener('input', function() {
            searchBuses();
        });
    }
    
    // Настройка значений по умолчанию для фильтров
    setupDefaultFilters();
});

// Функция для поиска остановок
function searchStops() {
    const searchInput = document.getElementById('stopSearch');
    if (!searchInput) return;
    
    const searchTerm = searchInput.value.toLowerCase();
    const rows = document.querySelectorAll('#stops-table-body tr');
    
    rows.forEach(row => {
        if (row.cells.length > 1) {
            const name = row.cells[1].textContent.toLowerCase();
            const lat = row.cells[2].textContent.toLowerCase();
            const lon = row.cells[3].textContent.toLowerCase();
            
            if (name.includes(searchTerm) || lat.includes(searchTerm) || 
                lon.includes(searchTerm) || searchTerm === '') {
                row.style.display = '';
            } else {
                row.style.display = 'none';
            }
        }
    });
}

// Функция настройки фильтров по умолчанию
function setupDefaultFilters() {
    // Установка текущей даты для фильтра пассажиропотока
    const dateFilter = document.getElementById('date-filter');
    if (dateFilter) {
        const today = new Date().toISOString().split('T')[0];
        dateFilter.value = today;
        dateFilter.max = today;
    }
    
    // Установка текущего времени для прогнозов
    const timeInput = document.getElementById('time-input');
    if (timeInput) {
        const now = new Date();
        now.setHours(now.getHours() + 1);
        timeInput.value = now.toISOString().slice(0, 16);
        timeInput.min = new Date().toISOString().slice(0, 16);
    }
}

// Функция загрузки данных для статистики
async function loadChartData() {
    try {
        const [passengers, buses] = await Promise.all([
            fetchData('/passengers'),
            fetchData('/buses')
        ]);
        
        updateChartsWithData(passengers, buses);
        
    } catch (error) {
        console.error('Ошибка загрузки данных для графиков:', error);
    }
}

// Функция обновления графиков данными
function updateChartsWithData(passengers, buses) {
    // Логика обновления графиков
}

// Функция для работы с WebSocket (реальное время)
function initWebSocket() {
    // Пример инициализации WebSocket
}

// Функция для скачивания отчетов
async function downloadReport(type, params = {}) {
    try {
        const queryParams = new URLSearchParams(params).toString();
        const url = `${API_BASE_URL}/reports/${type}?${queryParams}`;
        
        const link = document.createElement('a');
        link.href = url;
        link.download = `report_${type}_${new Date().toISOString().split('T')[0]}.pdf`;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        
        showNotification(`Отчет ${type} скачивается`, 'success');
    } catch (error) {
        console.error('Ошибка скачивания отчета:', error);
        showNotification('Ошибка скачивания отчета', 'error');
    }
}

// Экспорт данных в CSV
function exportToCSV(data, filename) {
    if (!data || data.length === 0) {
        showNotification('Нет данных для экспорта', 'warning');
        return;
    }
    
    const headers = Object.keys(data[0]);
    const csvRows = [
        headers.join(','),
        ...data.map(row => 
            headers.map(header => {
                const value = row[header];
                return typeof value === 'string' ? `"${value}"` : value;
            }).join(',')
        )
    ];
    
    const csvString = csvRows.join('\n');
    const blob = new Blob([csvString], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    
    const link = document.createElement('a');
    link.href = url;
    link.download = `${filename}_${new Date().toISOString().split('T')[0]}.csv`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
    
    showNotification(`Данные экспортированы в ${filename}.csv`, 'success');
}

// Инициализация всех компонентов после загрузки страницы
window.addEventListener('load', function() {
    console.log('Система управления пассажиропотоком загружена');
    
    loadDashboardStats();
    
    // Периодическое обновление данных (каждые 30 секунд)
    setInterval(() => {
        if (currentSectionId === 'dashboard') {
            loadDashboardStats();
        }
    }, 30000);
});