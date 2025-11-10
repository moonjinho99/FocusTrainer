let targetSeconds = 0;
let remaining = 0;
let timerInterval = null;
let stream = null;
let animationFrame = null;
let startTime = null;
let endTime = null;

// 집중도 분석 변수
let prevFrame = null;
let moveCount = 0;
let noPersonTimer = 0;
let totalFrames = 0;

const MOVE_THRESHOLD = 15;

const timer = document.getElementById('timer');
const startBtn = document.getElementById('startBtn');
const stopBtn = document.getElementById('stopBtn');
const resultSection = document.getElementById('resultSection');
const focusResult = document.getElementById('focusResult');
const cameraView = document.getElementById('cameraView');

const mainBtn = document.createElement('button');
mainBtn.textContent = "메인으로";
mainBtn.id = "mainBtn";
mainBtn.style.display = "none";

const saveBtn = document.createElement('button');
saveBtn.textContent = "저장하기";
saveBtn.id = "saveBtn";
saveBtn.style.display = "none";

// “메인으로” 버튼 이벤트
mainBtn.onclick = async () => {
  const token = localStorage.getItem("accessToken");
  try {
    const htmlResponse = await fetch('/focus/main', {
      method: 'GET',
      headers: { 'Authorization': `Bearer ${token}` }
    });
    if (htmlResponse.status === 401) {
      alert('세션이 만료되었습니다.');
      localStorage.clear();
      window.location.href = '/auth/login';
      return;
    }
    const html = await htmlResponse.text();
    document.open();
    document.write(html);
    document.close();
  } catch (error) {
    console.error("메인 페이지 요청 오류:", error);
    alert("서버 통신 중 문제가 발생했습니다.");
  }
};
document.body.appendChild(mainBtn);
document.body.appendChild(saveBtn);

// 목표시간 설정
window.addEventListener('load', () => {
  if (initMinutes > 0) {
    targetSeconds = initMinutes * 60;
    remaining = targetSeconds;
    updateTimerDisplay();
  }
});

// 카메라 켜기
async function activateCamera() {
  try {
    stream = await navigator.mediaDevices.getUserMedia({ video: true });
    cameraView.srcObject = stream;
    detectActivity();
  } catch (e) {
    alert('카메라 접근이 거부되었습니다.');
  }
}

// 카메라 끄기
function stopCamera() {
  if (animationFrame) cancelAnimationFrame(animationFrame);
  if (stream) {
    stream.getTracks().forEach(track => track.stop());
    stream = null;
    cameraView.srcObject = null;
  }
}

// 프레임 분석
const canvas = document.createElement('canvas');
const ctx = canvas.getContext('2d');
function detectActivity() {
  if (!cameraView.videoWidth) {
    animationFrame = requestAnimationFrame(detectActivity);
    return;
  }
  canvas.width = cameraView.videoWidth;
  canvas.height = cameraView.videoHeight;
  ctx.drawImage(cameraView, 0, 0, canvas.width, canvas.height);
  const frame = ctx.getImageData(0, 0, canvas.width, canvas.height);

  if (prevFrame) {
    let diff = 0;
    let bright = 0;

    for (let i = 0; i < frame.data.length; i += 8) {
      const avg1 = (frame.data[i] + frame.data[i + 1] + frame.data[i + 2]) / 3;
      const avg2 = (prevFrame.data[i] + prevFrame.data[i + 1] + prevFrame.data[i + 2]) / 3;
      diff += Math.abs(avg1 - avg2);
      bright += avg1;
    }

    const avgDiff = diff / (frame.data.length / 4);
    const avgBright = bright / (frame.data.length / 4);

    if (avgDiff > MOVE_THRESHOLD) moveCount++;
    if (avgBright < 10) noPersonTimer++;
    totalFrames++;
  }

  prevFrame = frame;
  animationFrame = requestAnimationFrame(detectActivity);
}

function updateTimerDisplay() {
  const hrs = String(Math.floor(remaining / 3600)).padStart(2, '0');
  const mins = String(Math.floor((remaining % 3600) / 60)).padStart(2, '0');
  const secs = String(remaining % 60).padStart(2, '0');
  timer.textContent = `${hrs}:${mins}:${secs}`;
}

// ✅ 훈련 시작
startBtn.addEventListener('click', async () => {
  if (remaining <= 0) return;
  startBtn.disabled = true;
  stopBtn.disabled = false;

  startTime = new Date(); // 시작시각 기록
  await activateCamera();

  timerInterval = setInterval(() => {
    remaining--;
    updateTimerDisplay();
    if (remaining <= 0) finishTraining();
  }, 1000);
});

// ✅ 훈련 종료
stopBtn.addEventListener('click', finishTraining);

function finishTraining() {
  clearInterval(timerInterval);
  stopCamera();
  startBtn.disabled = true;
  stopBtn.disabled = true;

  endTime = new Date();

  // 집중도 계산
  const absenceRatio = noPersonTimer / totalFrames;
  const moveRatio = moveCount / totalFrames;
  const distractionRatio = Math.min(1, absenceRatio + moveRatio);
  const focusScore = Math.max(0, (1 - distractionRatio) * 100);

  // ✅ 결과 영역 구성
  resultSection.style.display = "flex";
  resultSection.style.flexDirection = "column";
  resultSection.style.alignItems = "center";
  resultSection.style.marginTop = "30px";

  focusResult.textContent = `집중도 ${focusScore.toFixed(1)}%`;

  // ✅ 훈련명 입력 + 버튼들
  const input = document.createElement("input");
  input.type = "text";
  input.placeholder = "훈련명을 입력하세요";
  input.id = "trainingName";
  input.classList.add("training-input");

  saveBtn.classList.add("action-btn");
  saveBtn.style.display = "inline-block";
  saveBtn.textContent = "저장하기";

  mainBtn.classList.add("action-btn");
  mainBtn.style.display = "inline-block";
  mainBtn.textContent = "메인으로";

  // ✅ resultSection 내부에 정렬
  resultSection.appendChild(input);
  const btnWrap = document.createElement("div");
  btnWrap.classList.add("btn-group");
  btnWrap.appendChild(saveBtn);
  btnWrap.appendChild(mainBtn);
  resultSection.appendChild(btnWrap);

  // ✅ 저장 버튼 클릭 이벤트
  saveBtn.onclick = async () => {
    const trainingName = input.value.trim();
    if (!trainingName) {
      alert("훈련명을 입력해주세요.");
      return;
    }

    const token = localStorage.getItem("accessToken");
    const totalSeconds = Math.floor((endTime - startTime) / 1000);

    const payload = {
      name: trainingName,
      startTime: startTime.toISOString(),
      endTime: endTime.toISOString(),
      targetMinute: targetSeconds / 60,
      totalSeconds: totalSeconds,
      focusPercent: focusScore.toFixed(1)
    };

    try {
      const response = await fetch("/api/focus/save", {
        method: "POST",
        headers: {
          "Authorization": `Bearer ${token}`,
          "Content-Type": "application/json"
        },
        body: JSON.stringify(payload)
      });

      if (response.ok) {
        alert("훈련 기록이 저장되었습니다!");
        saveBtn.disabled = true;
      } else {
        alert("저장 중 오류가 발생했습니다.");
      }
    } catch (error) {
      console.error("저장 요청 오류:", error);
      alert("서버 통신 오류입니다.");
    }
  };
}

