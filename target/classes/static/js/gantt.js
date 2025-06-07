class GanttChart {
    constructor(containerId, tasks) {
        this.container = document.getElementById(containerId);
        this.tasks = tasks;
        this.cellWidth = 100;
        this.init();
    }
    
    init() {
        if (!this.tasks || this.tasks.length === 0) {
            this.container.innerHTML = '<div class="no-gantt-data">開始日・終了日が設定されたタスクがありません</div>';
            return;
        }
        
        this.calculateDateRange();
        this.render();
    }
    
    calculateDateRange() {
        const dates = [];
        
        this.tasks.forEach(task => {
            if (task.startDate) {
                dates.push(new Date(task.startDate));
            }
            if (task.endDate) {
                dates.push(new Date(task.endDate));
            }
        });
        
        if (dates.length === 0) {
            this.container.innerHTML = '<div class="no-gantt-data">開始日・終了日が設定されたタスクがありません</div>';
            return;
        }
        
        this.startDate = new Date(Math.min(...dates));
        this.endDate = new Date(Math.max(...dates));
        
        // 前後に余裕を持たせる
        this.startDate.setDate(this.startDate.getDate() - 3);
        this.endDate.setDate(this.endDate.getDate() + 3);
        
        this.totalDays = Math.ceil((this.endDate - this.startDate) / (1000 * 60 * 60 * 24));
    }
    
    render() {
        this.container.innerHTML = `
            <div class="gantt-chart">
                ${this.renderHeader()}
                ${this.renderTasks()}
            </div>
            ${this.renderLegend()}
        `;
        
        this.addTodayLine();
    }
    
    renderHeader() {
        let dateHeaders = '';
        
        for (let i = 0; i < this.totalDays; i++) {
            const date = new Date(this.startDate);
            date.setDate(date.getDate() + i);
            
            const dateStr = date.toLocaleDateString('ja-JP', {
                month: 'numeric',
                day: 'numeric'
            });
            
            dateHeaders += `<div class="gantt-date-header">${dateStr}</div>`;
        }
        
        return `
            <div class="gantt-timeline">
                <div class="gantt-task-header">タスク</div>
                <div class="gantt-timeline-header">
                    ${dateHeaders}
                </div>
            </div>
        `;
    }
    
    renderTasks() {
        return this.tasks.filter(task => task.startDate || task.endDate).map(task => {
            return `
                <div class="gantt-row">
                    <div class="gantt-task-info">
                        <div class="gantt-task-title">
                            ${task.title}
                            <span class="gantt-task-priority priority-${task.priority === 3 ? 'high' : task.priority === 2 ? 'medium' : 'low'}">
                                ${task.priority === 3 ? '高' : task.priority === 2 ? '中' : '低'}
                            </span>
                        </div>
                        <div class="gantt-task-dates">
                            ${this.formatTaskDates(task)}
                        </div>
                    </div>
                    <div class="gantt-timeline-area">
                        ${this.renderTimelineCells()}
                        ${this.renderTaskBar(task)}
                    </div>
                </div>
            `;
        }).join('');
    }
    
    renderTimelineCells() {
        let cells = '';
        for (let i = 0; i < this.totalDays; i++) {
            cells += '<div class="gantt-timeline-cell"></div>';
        }
        return cells;
    }
    
    renderTaskBar(task) {
        if (!task.startDate && !task.endDate) {
            return '';
        }
        
        let taskStart = task.startDate ? new Date(task.startDate) : new Date(this.startDate);
        let taskEnd = task.endDate ? new Date(task.endDate) : new Date(taskStart);
        
        if (!task.startDate && task.endDate) {
            taskStart = new Date(task.endDate);
            taskEnd = new Date(task.endDate);
        }
        
        const startDays = Math.floor((taskStart - this.startDate) / (1000 * 60 * 60 * 24));
        const duration = Math.max(1, Math.ceil((taskEnd - taskStart) / (1000 * 60 * 60 * 24)) + 1);
        
        const left = startDays * this.cellWidth;
        const width = duration * this.cellWidth - 4;
        
        const statusClass = task.status ? task.status.toLowerCase() : 'todo';
        
        return `
            <div class="gantt-bar status-${statusClass}" 
                 style="left: ${left}px; width: ${width}px;"
                 title="${task.title} (${task.progress}%)">
                <div class="gantt-progress" style="width: ${task.progress}%"></div>
                <div class="gantt-bar-text">${task.title}</div>
            </div>
        `;
    }
    
    formatTaskDates(task) {
        const options = { month: 'numeric', day: 'numeric' };
        
        if (task.startDate && task.endDate) {
            const start = new Date(task.startDate).toLocaleDateString('ja-JP', options);
            const end = new Date(task.endDate).toLocaleDateString('ja-JP', options);
            return `${start} - ${end}`;
        } else if (task.startDate) {
            const start = new Date(task.startDate).toLocaleDateString('ja-JP', options);
            return `開始: ${start}`;
        } else if (task.endDate) {
            const end = new Date(task.endDate).toLocaleDateString('ja-JP', options);
            return `終了: ${end}`;
        }
        
        return '';
    }
    
    addTodayLine() {
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        
        if (today >= this.startDate && today <= this.endDate) {
            const todayDays = Math.floor((today - this.startDate) / (1000 * 60 * 60 * 24));
            const left = todayDays * this.cellWidth + this.cellWidth / 2;
            
            const timelineAreas = this.container.querySelectorAll('.gantt-timeline-area');
            timelineAreas.forEach(area => {
                const todayLine = document.createElement('div');
                todayLine.className = 'gantt-today-line';
                todayLine.style.left = left + 'px';
                todayLine.title = '今日';
                area.appendChild(todayLine);
            });
        }
    }
    
    renderLegend() {
        return `
            <div class="gantt-legend">
                <div class="gantt-legend-item">
                    <div class="gantt-legend-color status-todo"></div>
                    <span>未開始</span>
                </div>
                <div class="gantt-legend-item">
                    <div class="gantt-legend-color status-in_progress"></div>
                    <span>進行中</span>
                </div>
                <div class="gantt-legend-item">
                    <div class="gantt-legend-color status-completed"></div>
                    <span>完了</span>
                </div>
                <div class="gantt-legend-item">
                    <div class="gantt-legend-color status-cancelled"></div>
                    <span>キャンセル</span>
                </div>
                <div class="gantt-legend-item">
                    <div class="gantt-today-line" style="position: static; width: 20px; height: 2px;"></div>
                    <span>今日</span>
                </div>
            </div>
        `;
    }
}