const body = document.body;

// 设置初始颜色
let baseColor1 = 200; // 主色调的色相（深蓝色）
let baseColor2 = 220; // 辅助色调的色相（深紫色）
let baseColor3 = 240; // 第三个颜色的色相（深蓝紫色）
let saturation = 20;  // 较低的饱和度，保持暗色调
let lightness = 10;   // 较低的亮度，接近黑色

/**
 * 更新背景渐变色
 */
function updateBackground() {
    document.body.style.background = `linear-gradient(
        45deg,
        hsl(${baseColor1}, ${saturation}%, ${lightness}%),
        hsl(${baseColor2}, ${saturation}%, ${lightness}%),
        hsl(${baseColor3}, ${saturation}%, ${lightness}%)
    )`;
}

/**
 * 动画循环，使用 requestAnimationFrame 实现平滑动画
 */
function animateBackground() {
    // 缓慢调整色相值，实现颜色平滑过渡
    baseColor1 = (baseColor1 + 0.05) % 360; // 每帧增加0.05，变化非常缓慢
    baseColor2 = (baseColor2 + 0.03) % 360; // 不同速度，增加层次感
    baseColor3 = (baseColor3 + 0.01) % 360;

    // 稍微调整饱和度和亮度，保持暗色调
    saturation = 20 + Math.sin(Date.now() * 0.001) * 5; // 饱和度在15-25%之间波动
    lightness = 10 + Math.sin(Date.now() * 0.001) * 2;  // 亮度在8-12%之间波动

    updateBackground();

    // 请求下一帧动画
    requestAnimationFrame(animateBackground);
}

// 初始化背景
updateBackground(); // 立即设置初始背景
animateBackground(); // 开始动画循环

// 萤火虫光点效果
const fireflies = [];
const numFireflies = 50; // 萤火虫数量
const fireflyContainer = document.createElement('div');

// 设置萤火虫容器样式
fireflyContainer.style.position = 'fixed';
fireflyContainer.style.top = '0';
fireflyContainer.style.left = '0';
fireflyContainer.style.width = '100%';
fireflyContainer.style.height = '100%';
fireflyContainer.style.pointerEvents = 'none'; // 不阻挡鼠标交互
fireflyContainer.style.zIndex = '9999'; // 确保在最上层
document.body.appendChild(fireflyContainer);

// 创建萤火虫光点
function createFirefly() {
    return {
        element: null, // 光点的 DOM 元素
        x: Math.random() * window.innerWidth, // 随机初始位置
        y: Math.random() * window.innerHeight,
        size: Math.random() * 4 + 2, // 随机大小
        opacity: Math.random(), // 随机透明度
        speedX: (Math.random() - 0.5) * 2, // 随机水平速度
        speedY: (Math.random() - 0.5) * 2, // 随机垂直速度
        life: Math.random() * 3000 + 1000, // 生命周期（1-4秒）
        fadeOut: function () {
            // 淡出效果
            let opacity = parseFloat(this.element.style.opacity);
            const interval = setInterval(() => {
                opacity -= 0.05;
                if (opacity <= 0) {
                    clearInterval(interval);
                    this.element.remove(); // 移除光点
                } else {
                    this.element.style.opacity = opacity;
                }
            }, 50);
        },
    };
}

// 初始化萤火虫
for (let i = 0; i < numFireflies; i++) {
    const firefly = createFirefly();
    fireflies.push(firefly);

    // 创建光点元素
    firefly.element = document.createElement('div');
    firefly.element.style.position = 'absolute';
    firefly.element.style.left = `${firefly.x}px`;
    firefly.element.style.top = `${firefly.y}px`;
    firefly.element.style.width = `${firefly.size}px`;
    firefly.element.style.height = `${firefly.size}px`;
    firefly.element.style.borderRadius = '50%';
    firefly.element.style.backgroundColor = 'rgba(255, 255, 200, 0.8)'; // 黄白色光点
    firefly.element.style.opacity = firefly.opacity;
    firefly.element.style.boxShadow = '0 0 10px rgba(255, 255, 200, 0.5)'; // 添加光晕效果
    fireflyContainer.appendChild(firefly.element);
}

/**
 * 更新萤火虫光点
 */
function updateFireflies() {
    fireflies.forEach((firefly, index) => {
        // 更新光点位置
        firefly.x += firefly.speedX;
        firefly.y += firefly.speedY;

        // 边界检测，光点反弹
        if (firefly.x < 0 || firefly.x > window.innerWidth) {
            firefly.speedX *= -1;
        }
        if (firefly.y < 0 || firefly.y > window.innerHeight) {
            firefly.speedY *= -1;
        }

        // 更新光点位置
        firefly.element.style.left = `${firefly.x}px`;
        firefly.element.style.top = `${firefly.y}px`;

        // 减少生命周期
        firefly.life -= 50;

        // 如果生命周期结束，移除光点
        if (firefly.life <= 0) {
            firefly.fadeOut();
            fireflies.splice(index, 1); // 从数组中移除
        }
    });

    // 如果需要持续生成新的光点，可以在这里添加逻辑
    if (fireflies.length < numFireflies) {
        const newFirefly = createFirefly();
        fireflies.push(newFirefly);

        // 创建光点元素
        newFirefly.element = document.createElement('div');
        newFirefly.element.style.position = 'absolute';
        newFirefly.element.style.left = `${newFirefly.x}px`;
        newFirefly.element.style.top = `${newFirefly.y}px`;
        newFirefly.element.style.width = `${newFirefly.size}px`;
        newFirefly.element.style.height = `${newFirefly.size}px`;
        newFirefly.element.style.borderRadius = '50%';
        newFirefly.element.style.backgroundColor = 'rgba(255, 255, 200, 0.8)';
        newFirefly.element.style.opacity = newFirefly.opacity;
        newFirefly.element.style.boxShadow = '0 0 10px rgba(255, 255, 200, 0.5)';
        fireflyContainer.appendChild(newFirefly.element);
    }
}

// 动画循环中更新萤火虫
function animate() {
    updateFireflies();
    requestAnimationFrame(animate);
}

// 开始萤火虫动画
animate();