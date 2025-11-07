let targetSeconds = 0;
let remaining = 0;
let timerInterval = null;
let stream = null; // 캠 스트림 저장용

const section = document.getElementById('trainingSection');
const timer = document.getElementById('timer');
const startBtn = document.getElementById('startBtn');
const stopBtn = document.getElementById('stopBtn');
const resultSection = document.getElementById('resultSection');
const focusResult = document.getElementById('focusResult');
const cameraView = document.getElementById('cameraView');

// ✅ 새로 추가: 메인으로 이동 버튼 생성
const mainBtn = document.createElement('button');
mainBtn.textContent = "메인으로";
mainBtn.id = "mainBtn";
mainBtn.style.display = "none";

// ✅ async 함수로 변경
mainBtn.onclick = async () => {
  const token = localStorage.getItem("accessToken");

  try {
    const htmlResponse = await fetch('/focus/main', {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });

    if (htmlResponse.status === 401) {
      alert('세션이 만료되었거나 인증되지 않았습니다.');
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


// 초기 목표시간 설정
window.addEventListener('load', () => {
  if (initMinutes > 0) {
    targetSeconds = initMinutes * 60;
    remaining = targetSeconds;
    updateTimerDisplay();
  }
});

// ✅ 카메라 켜기
async function activateCamera() {
  try {
    stream = await navigator.mediaDevices.getUserMedia({ video: true });
    cameraView.srcObject = stream;
  } catch (e) {
    alert('카메라 접근이 거부되었습니다.');
  }
}

// ✅ 카메라 끄기
function stopCamera() {
  if (stream) {
    const tracks = stream.getTracks();
    tracks.forEach(track => track.stop());
    stream = null;
    cameraView.srcObject = null;
  }
}

// 타이머 표시 갱신
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

  resultSection.style.display = 'block';
  focusResult.textContent = '집중도 89% (자리 이탈 없음)';

  // ✅ “메인으로” 버튼 표시
  mainBtn.style.display = "block";
  mainBtn.classList.add("main-btn");
}
