// 時間追跡JavaScript機能

// 時間追跡ボタンの初期化
function initTimeTrackingButtons() {
    const startButtons = document.querySelectorAll('.time-start-btn');
    const stopButtons = document.querySelectorAll('.time-stop-btn');
    
    startButtons.forEach(button => {
        button.addEventListener('click', function() {
            const taskId = this.getAttribute('data-task-id');
            startTimeTracking(taskId, this);
        });
    });
    
    stopButtons.forEach(button => {
        button.addEventListener('click', function() {
            const taskId = this.getAttribute('data-task-id');
            stopTimeTracking(taskId, this);
        });
    });
}

// タスクの時間追跡開始
async function startTimeTracking(taskId, button) {
    try {
        const response = await fetch(`/time/start/${taskId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        
        if (response.ok) {
            button.textContent = '停止';
            button.className = 'btn btn-danger time-stop-btn';
            showNotification('タスクを開始しました', 'success');
            
            // ボタンのイベントリスナーを更新
            button.removeEventListener('click', arguments.callee);
            button.addEventListener('click', function() {
                stopTimeTracking(taskId, this);
            });
        } else {
            const message = await response.text();
            showNotification(message, 'error');
        }
    } catch (error) {
        showNotification('エラーが発生しました', 'error');
        console.error('Error starting task:', error);
    }
}

// タスクの時間追跡停止
async function stopTimeTracking(taskId, button) {
    try {
        const response = await fetch(`/time/stop/${taskId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        
        if (response.ok) {
            button.textContent = '開始';
            button.className = 'btn btn-success time-start-btn';
            showNotification('タスクを停止しました', 'success');
            
            // ページをリロードして最新の実績時間を表示
            setTimeout(() => {
                window.location.reload();
            }, 1000);
        } else {
            const message = await response.text();
            showNotification(message, 'error');
        }
    } catch (error) {
        showNotification('エラーが発生しました', 'error');
        console.error('Error stopping task:', error);
    }
}

// 通知表示
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.textContent = message;
    
    // スタイルを設定
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 15px 20px;
        border-radius: 5px;
        color: white;
        font-weight: bold;
        z-index: 1000;
        transition: opacity 0.3s;
    `;
    
    // タイプに応じて背景色を設定
    switch(type) {
        case 'success':
            notification.style.backgroundColor = '#28a745';
            break;
        case 'error':
            notification.style.backgroundColor = '#dc3545';
            break;
        case 'warning':
            notification.style.backgroundColor = '#ffc107';
            notification.style.color = '#212529';
            break;
        default:
            notification.style.backgroundColor = '#007bff';
    }
    
    document.body.appendChild(notification);
    
    // 3秒後に自動削除
    setTimeout(() => {
        notification.style.opacity = '0';
        setTimeout(() => {
            if (notification.parentNode) {
                notification.parentNode.removeChild(notification);
            }
        }, 300);
    }, 3000);
}

// 時間推移チャートの初期化
function initTimeChart(data) {
    const canvas = document.getElementById('timeChart');
    if (!canvas || !data || data.length === 0) return;
    
    const ctx = canvas.getContext('2d');
    const labels = data.map(item => `${item.yearMonth.year}/${String(item.yearMonth.monthValue).padStart(2, '0')}`);
    const budgetData = data.map(item => item.budgetHours || 0);
    const actualData = data.map(item => item.actualHours || 0);
    
    // 簡単なチャート実装
    drawLineChart(ctx, canvas.width, canvas.height, labels, [
        { label: '予算時間', data: budgetData, color: '#007bff' },
        { label: '実績時間', data: actualData, color: '#28a745' }
    ]);
}

// 効率性チャートの初期化
function initEfficiencyChart(data) {
    const canvas = document.getElementById('efficiencyChart');
    if (!canvas || !data || data.length === 0) return;
    
    const ctx = canvas.getContext('2d');
    const labels = data.map(item => `${item.yearMonth.year}/${String(item.yearMonth.monthValue).padStart(2, '0')}`);
    const efficiencyData = data.map(item => item.efficiency || 0);
    
    drawLineChart(ctx, canvas.width, canvas.height, labels, [
        { label: '効率性(%)', data: efficiencyData, color: '#ffc107' }
    ]);
}

// シンプルな折れ線グラフ描画
function drawLineChart(ctx, width, height, labels, datasets) {
    const padding = 60;
    const chartWidth = width - padding * 2;
    const chartHeight = height - padding * 2;
    
    // 背景をクリア
    ctx.clearRect(0, 0, width, height);
    ctx.fillStyle = '#ffffff';
    ctx.fillRect(0, 0, width, height);
    
    // 最大値を計算
    const allValues = datasets.flatMap(dataset => dataset.data);
    const maxValue = Math.max(...allValues, 100); // 最低100
    const minValue = Math.min(...allValues, 0);
    
    // グリッド線を描画
    ctx.strokeStyle = '#e9ecef';
    ctx.lineWidth = 1;
    
    // 縦のグリッド線
    for (let i = 0; i <= labels.length - 1; i++) {
        const x = padding + (chartWidth / (labels.length - 1)) * i;
        ctx.beginPath();
        ctx.moveTo(x, padding);
        ctx.lineTo(x, height - padding);
        ctx.stroke();
    }
    
    // 横のグリッド線
    for (let i = 0; i <= 5; i++) {
        const y = padding + (chartHeight / 5) * i;
        ctx.beginPath();
        ctx.moveTo(padding, y);
        ctx.lineTo(width - padding, y);
        ctx.stroke();
    }
    
    // データセットを描画
    datasets.forEach((dataset, datasetIndex) => {
        ctx.strokeStyle = dataset.color;
        ctx.lineWidth = 3;
        ctx.beginPath();
        
        dataset.data.forEach((value, index) => {
            const x = padding + (chartWidth / (labels.length - 1)) * index;
            const y = height - padding - ((value - minValue) / (maxValue - minValue)) * chartHeight;
            
            if (index === 0) {
                ctx.moveTo(x, y);
            } else {
                ctx.lineTo(x, y);
            }
        });
        
        ctx.stroke();
        
        // データポイントを描画
        ctx.fillStyle = dataset.color;
        dataset.data.forEach((value, index) => {
            const x = padding + (chartWidth / (labels.length - 1)) * index;
            const y = height - padding - ((value - minValue) / (maxValue - minValue)) * chartHeight;
            
            ctx.beginPath();
            ctx.arc(x, y, 4, 0, 2 * Math.PI);
            ctx.fill();
        });
    });
    
    // ラベルを描画
    ctx.fillStyle = '#333';
    ctx.font = '12px Arial';
    ctx.textAlign = 'center';
    
    // X軸ラベル
    labels.forEach((label, index) => {
        const x = padding + (chartWidth / (labels.length - 1)) * index;
        ctx.fillText(label, x, height - padding + 20);
    });
    
    // Y軸ラベル
    ctx.textAlign = 'right';
    for (let i = 0; i <= 5; i++) {
        const value = minValue + ((maxValue - minValue) / 5) * (5 - i);
        const y = padding + (chartHeight / 5) * i;
        ctx.fillText(Math.round(value), padding - 10, y + 4);
    }
    
    // 凡例を描画
    let legendY = 20;
    datasets.forEach((dataset, index) => {
        ctx.fillStyle = dataset.color;
        ctx.fillRect(width - 150, legendY, 15, 15);
        
        ctx.fillStyle = '#333';
        ctx.textAlign = 'left';
        ctx.font = '14px Arial';
        ctx.fillText(dataset.label, width - 130, legendY + 12);
        
        legendY += 25;
    });
}

// 月次データの取得
async function fetchMonthlyData(yearMonth) {
    try {
        const response = await fetch(`/time/api/monthly/${yearMonth}`);
        if (response.ok) {
            return await response.json();
        }
        return null;
    } catch (error) {
        console.error('Error fetching monthly data:', error);
        return null;
    }
}

// 時間フォーマット（分を時間:分に変換）
function formatMinutes(minutes) {
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    return `${hours}:${String(mins).padStart(2, '0')}`;
}

// 時間の差分計算
function calculateTimeDifference(startTime, endTime) {
    const start = new Date(startTime);
    const end = new Date(endTime);
    return Math.abs(end - start) / (1000 * 60); // 分で返す
}