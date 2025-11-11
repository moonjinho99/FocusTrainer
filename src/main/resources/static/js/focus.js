document.addEventListener("DOMContentLoaded", () => {
  // 로그인 유저 이름 표시
  const nickname = localStorage.getItem('nickname') || '사용자';
  document.getElementById('userName').innerText = `${nickname}님 안녕하세요!`;

  loadFocusRecords();
});

// ✅ 훈련 기록 불러오기
async function loadFocusRecords() {
  const list = document.getElementById("focusRecords");
  list.innerHTML = "";

  try {
    const response = await fetch("/api/focus/records", {
      headers: {
        "Authorization": `Bearer ${localStorage.getItem("accessToken")}`
      }
    });

    if (!response.ok) throw new Error("기록 조회 실패");

    const data = await response.json();
    const diaries = data.data || [];

    if (diaries.length === 0) {
      list.innerHTML = "<p>아직 훈련 기록이 없습니다.</p>";
      return;
    }

    diaries.forEach(diary => {

      const li = document.createElement("li");
      li.classList.add("record-item");
      li.innerHTML = `
        <div class="record-summary">
          <div>
            <strong>${diary.name}</strong>
            <span>${new Date(diary.createdAt).toLocaleDateString()}</span>
          </div>
          <button class="delete-btn" onclick="deleteRecord(${diary.diaryId})">삭제</button>
        </div>

        <div class="record-detail">
          <p>시작 시간: ${formatDate(diary.startTime)}</p>
          <p>종료 시간: ${formatDate(diary.endTime)}</p>
          <p>목표 시간: ${diary.targetMinute}분</p>
          <p>총 훈련 시간: ${formatSeconds(diary.totalSecond)}</p>
          <p>집중력: ${diary.focusPercent}%</p>
        </div>
      `;

      li.addEventListener("click", e => {
        if (e.target.classList.contains("delete-btn")) return;
        li.querySelector(".record-detail").classList.toggle("active");
      });

      list.appendChild(li);
    });

  } catch (error) {
    console.error(error);
    list.innerHTML = "<p>기록을 불러오지 못했습니다.</p>";
  }
}

// ✅ 기록 삭제
async function deleteRecord(diaryId) {
  if (!confirm("정말 삭제하시겠습니까?")) return;

  const token = localStorage.getItem("accessToken");

  try {
    const res = await fetch(`/api/focus/${diaryId}`, {
      method: "DELETE",
      headers: { "Authorization": `Bearer ${token}` }
    });

    if (!res.ok) {
      alert("삭제 실패");
      return;
    }

    alert("삭제되었습니다.");
    loadFocusRecords(); // 다시 목록 새로고침

  } catch (err) {
    console.error(err);
    alert("서버 오류가 발생했습니다.");
  }
}

// 시간 형식 변환 유틸
function formatDate(dateString) {
  if (!dateString) return "-";
  const d = new Date(dateString);
  return `${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}`;
}

// 초 단위를 시:분:초로 변환
function formatSeconds(seconds) {
  if (!seconds) return "-";
  const hrs = Math.floor(seconds / 3600);
  const mins = Math.floor((seconds % 3600) / 60);
  const secs = seconds % 60;
  return `${hrs.toString().padStart(2,'0')}:${mins.toString().padStart(2,'0')}:${secs.toString().padStart(2,'0')}`;
}


// ✅ 훈련 시작 버튼 클릭
async function startTraining() {
  const minutes = prompt("목표 시간을 분 단위로 입력하세요:", "30");
  if (!minutes || isNaN(minutes) || minutes <= 0) {
    alert("올바른 시간을 입력해주세요.");
    return;
  }

  const token = localStorage.getItem("accessToken");
  if (!token) {
    alert("로그인이 필요합니다.");
    window.location.href = "/auth/login";
    return;
  }

  try {
    const htmlResponse = await fetch(`/focus/start?minutes=${encodeURIComponent(minutes)}`, {
      method: "GET",
      headers: {
        "Authorization": `Bearer ${token}` // ✅ Content-Type 제거
      }
    });

    if (htmlResponse.status === 401) {
      alert("인증이 만료되었습니다. 다시 로그인해주세요.");
      localStorage.clear();
      window.location.href = "/auth/login";
      return;
    }

    if (!htmlResponse.ok) {
      alert("서버 오류가 발생했습니다.");
      return;
    }

    const html = await htmlResponse.text();
    document.open();
    document.write(html);
    document.close();

  } catch (error) {
    console.error("훈련 시작 중 오류:", error);
    alert("서버 통신 중 문제가 발생했습니다.");
  }
}


// ✅ 로그아웃
async function logout() {
  const token = localStorage.getItem('accessToken');
  await fetch('/api/auth/logout', {
    method: 'POST',
    headers: { 'Authorization': `Bearer ${token}` }
  });

  localStorage.clear();
  alert("로그아웃 합니다.");
  window.location.href = '/auth/login';
}
