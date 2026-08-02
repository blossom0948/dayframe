const STORAGE_KEY = "dayframe-web-entries-v1";
const THEME_KEY = "dayframe-web-theme-v1";

const TONE_BACKGROUNDS = {
  dawn: "linear-gradient(145deg, #f8d29b 0%, #ef9d92 45%, #6b78c7 100%)",
  table: "linear-gradient(145deg, #d89560 0%, #734c3c 47%, #202b45 100%)",
  green: "linear-gradient(145deg, #d8e89d 0%, #6ba875 42%, #213f54 100%)",
  night: "linear-gradient(145deg, #0e1a39 0%, #596d9e 46%, #e9bc93 100%)",
  coffee: "linear-gradient(145deg, #e1b07e 0%, #8b5345 49%, #302844 100%)",
  blue: "linear-gradient(145deg, #cfebf6 0%, #7da8d0 50%, #455178 100%)",
  rose: "linear-gradient(145deg, #f0c6c9 0%, #c46e83 49%, #4e3a62 100%)",
};

const MOODS = [
  ["😊", "좋음"],
  ["😌", "차분"],
  ["🤍", "평온"],
  ["🥳", "신남"],
  ["😮‍💨", "지침"],
];

const seedEntries = [
  { id: "seed-04", date: "2026-07-04", title: "풀밭에서 만난 오후", body: "잠깐 멈춰서 고양이와 햇빛을 나눠 가졌다.", mood: "평온", musicTitle: "Do the Dance", musicArtist: "아일릿", tone: "green", favorite: true },
  { id: "seed-07", date: "2026-07-07", title: "오늘의 작은 보상", body: "바삭한 치즈와 느린 점심. 좋아하는 걸 천천히 먹은 날.", mood: "좋음", musicTitle: "Day By Day", musicArtist: "Amaria", tone: "table", favorite: false },
  { id: "seed-15", date: "2026-07-15", title: "함께 먹는 저녁", body: "대화가 길어져서 식탁 위 불이 늦게까지 켜져 있었다.", mood: "좋음", musicTitle: "Warm Light", musicArtist: "The Paper Kites", tone: "rose", favorite: false },
  { id: "seed-18", date: "2026-07-18", title: "새로운 책상", body: "작은 변화 하나로 방의 공기가 달라졌다.", mood: "신남", musicTitle: "Bloom", musicArtist: "RÜFÜS DU SOL", tone: "blue", favorite: false },
  { id: "seed-23", date: "2026-07-23", title: "아이스 아메리카노", body: "오늘은 집중보다 쉬어 가는 쪽을 골랐다.", mood: "차분", musicTitle: "Home", musicArtist: "Haux", tone: "coffee", favorite: false },
  { id: "seed-26", date: "2026-07-26", title: "구름이 예쁜 날", body: "하늘을 오래 바라보면 생각이 조금 가벼워진다.", mood: "평온", musicTitle: "Sunset Lover", musicArtist: "Petit Biscuit", tone: "dawn", favorite: true },
  { id: "seed-30", date: "2026-07-30", title: "늦은 산책", body: "오늘 하루도 무사히 지나갔다. 그걸로 충분하다.", mood: "차분", musicTitle: "Holocene", musicArtist: "Bon Iver", tone: "night", favorite: false },
];

const state = {
  view: "calendar",
  month: new Date(2026, 6, 1),
  query: "",
  sortAsc: false,
  editorId: null,
  editorDraft: null,
};

const viewRoot = document.querySelector("#view-root");
const modalRoot = document.querySelector("#modal-root");
const searchInput = document.querySelector("#search-input");
const photoInput = document.querySelector("#photo-input");
const importInput = document.querySelector("#import-input");

let entries = loadEntries();

function loadEntries() {
  try {
    const parsed = JSON.parse(localStorage.getItem(STORAGE_KEY) || "null");
    return Array.isArray(parsed) ? parsed : seedEntries.map((entry) => ({ ...entry }));
  } catch {
    return seedEntries.map((entry) => ({ ...entry }));
  }
}

function persist() {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(entries));
}

function esc(value = "") {
  return String(value).replace(/[&<>'"]/g, (character) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", "'": "&#39;", '"': "&quot;" })[character]);
}

function iso(date) {
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")}`;
}

function parseDate(value) {
  const [year, month, day] = value.split("-").map(Number);
  return new Date(year, month - 1, day);
}

function dateLabel(value, withWeekday = true) {
  const date = parseDate(value);
  return new Intl.DateTimeFormat("ko-KR", withWeekday ? { year: "numeric", month: "long", day: "numeric", weekday: "short" } : { year: "numeric", month: "long" }).format(date);
}

function monthLabel(date = state.month) {
  return new Intl.DateTimeFormat("ko-KR", { year: "numeric", month: "long" }).format(date);
}

function backgroundFor(entry) {
  if (entry.image) return `url(${entry.image})`;
  return TONE_BACKGROUNDS[entry.tone] || TONE_BACKGROUNDS.blue;
}

function photoStyle(entry) {
  return `style="--photo-bg:${esc(backgroundFor(entry))}"`;
}

function currentMonthEntries() {
  const year = state.month.getFullYear();
  const month = state.month.getMonth();
  return entries.filter((entry) => {
    const date = parseDate(entry.date);
    return date.getFullYear() === year && date.getMonth() === month;
  });
}

function filteredEntries() {
  const query = state.query.trim().toLowerCase();
  const result = entries.filter((entry) => {
    if (!query) return true;
    return [entry.title, entry.body, entry.musicTitle, entry.musicArtist, entry.mood].some((value) => String(value || "").toLowerCase().includes(query));
  });
  return result.sort((a, b) => state.sortAsc ? a.date.localeCompare(b.date) : b.date.localeCompare(a.date));
}

function entryByDate(date) {
  return entries.find((entry) => entry.date === date);
}

function entryById(id) {
  return entries.find((entry) => String(entry.id) === String(id));
}

function monthProgress() {
  const now = new Date();
  const monthStart = new Date(state.month.getFullYear(), state.month.getMonth(), 1);
  const monthEnd = new Date(state.month.getFullYear(), state.month.getMonth() + 1, 0);
  let elapsed;
  if (monthStart > now) elapsed = 0;
  else if (monthEnd < now) elapsed = monthEnd.getDate();
  else elapsed = now.getDate();
  const covered = currentMonthEntries().filter((entry) => parseDate(entry.date) <= (monthEnd < now ? monthEnd : now)).length;
  return { covered, elapsed, percent: elapsed ? Math.min(100, Math.round((covered / elapsed) * 100)) : 0 };
}

function streaks() {
  const days = new Set(entries.map((entry) => entry.date));
  const sorted = [...days].sort();
  let longest = 0;
  let run = 0;
  let previous = null;
  for (const value of sorted) {
    const date = parseDate(value);
    if (previous && (date - previous) / 86400000 === 1) run += 1;
    else run = 1;
    longest = Math.max(longest, run);
    previous = date;
  }
  let current = 0;
  const cursor = new Date();
  while (days.has(iso(cursor))) {
    current += 1;
    cursor.setDate(cursor.getDate() - 1);
  }
  return { current, longest };
}

function pageTitle() {
  return { calendar: "이번 달의 장면", feed: "기록 피드", stats: "나의 기록 리듬", archive: "보관함" }[state.view] || "이번 달의 장면";
}

function render() {
  const storedTheme = localStorage.getItem(THEME_KEY);
  if (storedTheme === "dark") document.body.dataset.theme = "dark";
  else document.body.removeAttribute("data-theme");

  document.querySelector("#page-title").textContent = pageTitle();
  searchInput.value = state.query;
  document.querySelectorAll("[data-view]").forEach((button) => button.classList.toggle("is-active", button.dataset.view === state.view));
  viewRoot.innerHTML = state.view === "calendar" ? renderCalendar() : state.view === "feed" ? renderFeed() : state.view === "stats" ? renderStats() : renderArchive();
}

function renderCalendar() {
  const progress = monthProgress();
  const cells = [];
  const year = state.month.getFullYear();
  const month = state.month.getMonth();
  const firstWeekday = (new Date(year, month, 1).getDay() + 6) % 7;
  const daysInMonth = new Date(year, month + 1, 0).getDate();
  const previousMonthDays = new Date(year, month, 0).getDate();
  const totalCells = Math.ceil((firstWeekday + daysInMonth) / 7) * 7;
  const today = iso(new Date());

  for (let index = 0; index < totalCells; index += 1) {
    const dayNumber = index - firstWeekday + 1;
    let date;
    let outside = false;
    if (dayNumber < 1) {
      date = new Date(year, month - 1, previousMonthDays + dayNumber);
      outside = true;
    } else if (dayNumber > daysInMonth) {
      date = new Date(year, month + 1, dayNumber - daysInMonth);
      outside = true;
    } else {
      date = new Date(year, month, dayNumber);
    }
    const dateValue = iso(date);
    const entry = outside ? null : entryByDate(dateValue);
    const cellClasses = ["calendar-cell", outside ? "is-outside" : "", dateValue === today ? "is-today" : "", !outside && !entry ? "is-empty" : ""].filter(Boolean).join(" ");
    cells.push(`<div class="${cellClasses}" ${!outside && entry ? `data-entry-id="${esc(entry.id)}"` : !outside ? `data-action="create-for-date" data-date="${dateValue}"` : ""}>
      <div class="calendar-day"><span>${date.getDate()}</span>${dateValue === today && !outside ? '<i class="today-dot"></i>' : ""}</div>
      ${entry ? `<div class="calendar-photo" ${photoStyle(entry)}><span>${entry.mood || "오늘"}</span></div>` : ""}
    </div>`);
  }

  const recent = [...currentMonthEntries()].sort((a, b) => b.date.localeCompare(a.date)).slice(0, 3);
  return `<div class="view-heading">
    <div><h2>${esc(monthLabel())}</h2><p>사진 한 장씩 쌓아가는 나의 ${state.month.getMonth() + 1}월</p></div>
    <div class="heading-actions"><div class="month-switcher"><button data-action="previous-month" aria-label="이전 달">‹</button><span class="month-label">${esc(monthLabel())}</span><button data-action="next-month" aria-label="다음 달">›</button></div><button class="secondary-button" data-action="today">오늘</button></div>
  </div>
  <div class="overview-grid">
    <section class="card calendar-card" aria-label="${esc(monthLabel())} 달력">
      <div class="weekdays">${["월", "화", "수", "목", "금", "토", "일"].map((day) => `<span class="weekday">${day}</span>`).join("")}</div>
      <div class="calendar-grid">${cells.join("")}</div>
    </section>
    <aside class="card progress-card">
      <div><span class="card-kicker">MONTHLY FRAME</span><h3>${progress.covered}일의 장면</h3><p>${progress.elapsed ? `지나온 ${progress.elapsed}일 중 ${progress.covered}일을 사진으로 남겼어요.` : "아직 시작 전인 달이에요. 미리 장면을 준비해 보세요."}</p></div>
      <div class="progress-ring" style="--progress:${progress.percent}%"><strong>${progress.percent}%</strong><span>기록률</span></div>
      <div class="progress-stats"><div><strong>${currentMonthEntries().length}</strong><span>이번 달</span></div><div><strong>${streaks().current}</strong><span>현재 연속</span></div><div><strong>${streaks().longest}</strong><span>최장 기록</span></div></div>
    </aside>
  </div>
  <section class="card section-card"><h3>최근에 남긴 장면</h3><p>카드를 눌러 이야기를 다시 읽어보세요.</p>${recent.length ? `<div class="feed-list">${recent.map(renderFeedCard).join("")}</div>` : emptyState("아직 이 달의 기록이 없어요", "오늘의 사진 한 장으로 첫 장면을 만들어 보세요.")}</section>`;
}

function renderFeed() {
  const records = state.query.trim() ? filteredEntries() : filteredEntries().filter((entry) => currentMonthEntries().some((monthEntry) => monthEntry.id === entry.id));
  const sortLabel = state.sortAsc ? "최신순" : "오래된 순";
  return `<div class="view-heading"><div><h2>${esc(monthLabel())} 피드</h2><p>${state.query ? `“${esc(state.query)}” 검색 결과 ${records.length}개` : "스크롤만 내려도 지나온 달이 눈앞에 펼쳐져요."}</p></div><div class="heading-actions"><button class="secondary-button" data-action="sort-feed">${sortLabel} ↕</button><button class="primary-button compact" data-action="new-entry">＋ 기록하기</button></div></div>${records.length ? `<div class="feed-list">${records.map(renderFeedCard).join("")}</div>` : emptyState(state.query ? "검색 결과가 없어요" : "아직 이 달의 기록이 없어요", state.query ? "제목, 이야기, 음악을 다른 단어로 검색해 보세요." : "달력의 빈 날짜나 기록하기 버튼으로 시작해 보세요.")}`;
}

function renderFeedCard(entry) {
  return `<article class="feed-card" data-entry-id="${esc(entry.id)}"><div class="feed-photo" ${photoStyle(entry)}><span class="photo-date">${esc(dateLabel(entry.date, false))}</span></div><div class="feed-copy"><span class="feed-date">${esc(dateLabel(entry.date))}</span><h3>${esc(entry.title || "제목 없는 하루")}</h3><p>${esc(entry.body || "이 날의 이야기를 아직 적지 않았어요.")}</p><div class="feed-meta"><span class="music-line">${entry.musicTitle ? `♫ ${esc(entry.musicTitle)}` : `${esc(entry.mood || "오늘의 기록")}`}</span><span class="favorite-mark">${entry.favorite ? "♥" : "♡"}</span></div></div></article>`;
}

function renderStats() {
  const progress = monthProgress();
  const streak = streaks();
  const moodCounts = MOODS.map(([emoji, label]) => [emoji, label, entries.filter((entry) => entry.mood === label).length]).filter(([, , count]) => count);
  const recentDays = Array.from({ length: 7 }, (_, index) => {
    const date = new Date(state.month.getFullYear(), state.month.getMonth(), Math.max(1, new Date(state.month.getFullYear(), state.month.getMonth() + 1, 0).getDate() - 6 + index));
    const value = iso(date);
    return { label: `${date.getMonth() + 1}/${date.getDate()}`, count: entries.filter((entry) => entry.date === value).length };
  });
  const max = Math.max(1, ...recentDays.map((day) => day.count));
  return `<div class="view-heading"><div><h2>나의 기록 리듬</h2><p>사진을 남긴 날들이 만드는 작고 선명한 패턴</p></div><button class="secondary-button" data-view="calendar">달력으로 보기</button></div>
  <div class="stats-grid"><div class="metric-card"><span>전체 기록</span><strong>${entries.length}</strong><small>사진으로 남긴 하루</small></div><div class="metric-card"><span>이번 달 기록률</span><strong>${progress.percent}%</strong><small>${progress.covered}일 기록</small></div><div class="metric-card"><span>현재 연속</span><strong>${streak.current}</strong><small>오늘을 포함해 계산</small></div><div class="metric-card"><span>최장 연속</span><strong>${streak.longest}</strong><small>꾸준히 이어 온 날</small></div></div>
  <section class="card chart-card"><h3>${esc(monthLabel())} 기록 분포</h3><div class="bar-chart">${recentDays.map((day) => `<div class="bar-wrap"><div class="bar" style="height:${Math.max(8, (day.count / max) * 100)}%" title="${day.count}개"></div><span>${day.label}</span></div>`).join("")}</div><div class="mood-row">${moodCounts.length ? moodCounts.map(([emoji, label, count]) => `<span class="mood-chip">${emoji} ${label} · ${count}</span>`).join("") : '<span class="mood-chip">아직 기분 데이터가 없어요</span>'}</div></section>`;
}

function renderArchive() {
  const favorites = entries.filter((entry) => entry.favorite).sort((a, b) => b.date.localeCompare(a.date));
  return `<div class="view-heading"><div><h2>보관함</h2><p>유독 남기고 싶은 하루를 따로 모아두는 곳</p></div><div class="heading-actions"><button class="secondary-button" data-action="export-data">백업 내보내기</button><button class="secondary-button" data-action="import-data">백업 불러오기</button></div></div><div class="archive-layout"><section class="card archive-panel"><h3>마음에 남은 장면 <span class="count-badge">${favorites.length}</span></h3><p>달력의 하트 버튼으로 언제든 추가할 수 있어요.</p>${favorites.length ? `<div class="archive-list">${favorites.map((entry) => `<div class="archive-row" data-entry-id="${esc(entry.id)}"><div class="archive-thumb" ${photoStyle(entry)}></div><div><strong>${esc(entry.title || "제목 없는 하루")}</strong><span>${esc(dateLabel(entry.date))}</span></div><span class="favorite-mark" style="margin-left:auto">♥</span></div>`).join("")}</div>` : '<div class="archive-empty">아직 즐겨찾기한 장면이 없어요.<br />상세 화면에서 하트를 눌러보세요.</div>'}</section><aside class="card archive-panel"><h3>나의 기록</h3><p>브라우저 안에 안전하게 보관된 기록이에요.</p><div class="highlight-row"><div class="highlight"><strong>${entries.length}</strong><span>전체 사진</span></div><div class="highlight"><strong>${favorites.length}</strong><span>즐겨찾기</span></div><div class="highlight"><strong>${new Set(entries.map((entry) => entry.date.slice(0, 7))).size}</strong><span>기록한 달</span></div></div><div class="backup-actions" style="margin-top:16px"><button class="secondary-button" data-action="export-data">JSON으로 저장하기</button><button class="secondary-button" data-action="import-data">JSON 불러오기</button></div></aside></div>`;
}

function emptyState(title, message) {
  return `<div class="empty-state"><div><div class="empty-icon">＋</div><h3>${esc(title)}</h3><p>${esc(message)}</p><button class="primary-button compact" data-action="new-entry">첫 기록 만들기</button></div></div>`;
}

function openDetail(id) {
  const entry = entryById(id);
  if (!entry) return;
  modalRoot.innerHTML = `<div class="modal-backdrop" data-action="close-modal"><article class="modal detail-modal" role="dialog" aria-modal="true" aria-label="기록 상세"><div class="modal-head"><div><h2>기록 상세</h2><p>${esc(dateLabel(entry.date))}</p></div><button class="close-button" data-action="close-modal" aria-label="닫기">×</button></div><div class="detail-body"><div class="detail-photo" ${photoStyle(entry)}></div><div class="detail-copy"><span class="feed-date">${esc(entry.mood || "오늘의 하루")}</span><h2>${esc(entry.title || "제목 없는 하루")}</h2><p class="story">${esc(entry.body || "이 날의 이야기를 아직 적지 않았어요.")}</p>${entry.musicTitle ? `<div class="detail-music"><div class="music-art">♫</div><div><strong>${esc(entry.musicTitle)}</strong><span>${esc(entry.musicArtist || "나의 플레이리스트")}</span></div></div>` : ""}<div class="detail-actions"><button class="secondary-button" data-action="toggle-favorite" data-entry-id="${esc(entry.id)}">${entry.favorite ? "♥ 즐겨찾기" : "♡ 즐겨찾기"}</button><button class="secondary-button" data-action="share-entry" data-entry-id="${esc(entry.id)}">공유</button></div><div class="detail-actions"><button class="secondary-button" data-action="edit-entry" data-entry-id="${esc(entry.id)}">편집</button><button class="secondary-button" data-action="delete-entry" data-entry-id="${esc(entry.id)}">삭제</button></div></div></div></article></div>`;
}

function newDraft(entry = null, date = iso(state.month)) {
  return { date: entry?.date || date, title: entry?.title || "", body: entry?.body || "", mood: entry?.mood || "좋음", musicTitle: entry?.musicTitle || "", musicArtist: entry?.musicArtist || "", image: entry?.image || "", tone: entry?.tone || "dawn" };
}

function openEditor(id = null, date = iso(state.month)) {
  const entry = id ? entryById(id) : null;
  state.editorId = entry?.id || null;
  state.editorDraft = newDraft(entry, date);
  renderEditorModal();
}

function renderEditorModal(error = "") {
  const draft = state.editorDraft;
  const hasPhoto = Boolean(draft.image);
  const previewStyle = `style="--photo-bg:${esc(draft.image ? `url(${draft.image})` : TONE_BACKGROUNDS[draft.tone])}"`;
  modalRoot.innerHTML = `<div class="modal-backdrop" data-action="close-modal"><article class="modal" role="dialog" aria-modal="true" aria-label="${state.editorId ? "기록 편집" : "새 기록"}"><div class="modal-head"><div><h2>${state.editorId ? "기록 편집" : "오늘의 장면 남기기"}</h2><p>한 장의 사진에 오늘의 온도를 담아보세요.</p></div><button class="close-button" data-action="close-modal" aria-label="닫기">×</button></div><form class="editor-form" id="entry-form"><div class="editor-layout"><button class="upload-box ${hasPhoto ? "has-photo" : ""}" ${previewStyle} type="button" data-action="choose-photo"><span class="upload-box-content"><strong>${hasPhoto ? "사진 바꾸기" : "사진 선택하기"}</strong><span>${hasPhoto ? "다른 사진으로 교체할 수 있어요" : "JPG, PNG · 한 장이면 충분해요"}</span></span></button><div class="form-fields"><div class="form-row"><div class="form-field"><label for="entry-date">날짜</label><input id="entry-date" name="date" type="date" value="${esc(draft.date)}" required /></div><div class="form-field"><label for="entry-mood">기분</label><select id="entry-mood" name="mood">${MOODS.map(([emoji, label]) => `<option value="${label}" ${draft.mood === label ? "selected" : ""}>${emoji} ${label}</option>`).join("")}</select></div></div><div class="form-field"><label for="entry-title">제목</label><input id="entry-title" name="title" maxlength="60" value="${esc(draft.title)}" placeholder="오늘을 한 문장으로" /></div><div class="form-field"><label for="entry-body">이야기</label><textarea id="entry-body" name="body" maxlength="500" placeholder="사진을 찍은 순간을 적어보세요">${esc(draft.body)}</textarea></div><div class="form-row"><div class="form-field"><label for="music-title">음악</label><input id="music-title" name="musicTitle" maxlength="80" value="${esc(draft.musicTitle)}" placeholder="노래 제목" /></div><div class="form-field"><label for="music-artist">아티스트</label><input id="music-artist" name="musicArtist" maxlength="80" value="${esc(draft.musicArtist)}" placeholder="아티스트" /></div></div></div></div><div class="form-error">${esc(error)}</div><div class="form-actions"><button type="button" class="secondary-button" data-action="close-modal">취소</button><button type="submit" class="primary-button">${state.editorId ? "변경 저장" : "기록 저장"}</button></div></form></article></div>`;
}

function closeModal() {
  modalRoot.innerHTML = "";
  state.editorId = null;
  state.editorDraft = null;
}

function saveDraft() {
  const draft = state.editorDraft;
  if (!draft.date) return renderEditorModal("날짜를 선택해 주세요.");
  if (!draft.image && !state.editorId) return renderEditorModal("사진 한 장을 먼저 선택해 주세요.");
  const current = state.editorId ? entryById(state.editorId) : null;
  const record = { id: state.editorId || `entry-${Date.now()}`, date: draft.date, title: draft.title.trim(), body: draft.body.trim().slice(0, 500), mood: draft.mood, musicTitle: draft.musicTitle.trim(), musicArtist: draft.musicArtist.trim(), image: draft.image, tone: current?.tone || draft.tone || "dawn", favorite: current?.favorite || false, updatedAt: new Date().toISOString() };
  if (current) entries = entries.map((entry) => entry.id === current.id ? record : entry);
  else entries = [...entries, record];
  persist();
  state.month = new Date(record.date.slice(0, 7) + "-01T00:00:00");
  closeModal();
  render();
}

function toggleFavorite(id) {
  entries = entries.map((entry) => entry.id === id ? { ...entry, favorite: !entry.favorite } : entry);
  persist();
  render();
  openDetail(id);
}

function deleteEntry(id) {
  const entry = entryById(id);
  if (!entry || !window.confirm("이 기록을 삭제할까요?")) return;
  entries = entries.filter((item) => item.id !== id);
  persist();
  closeModal();
  render();
}

async function shareEntry(id) {
  const entry = entryById(id);
  if (!entry) return;
  const text = `${entry.title || "오늘의 하루"} · ${dateLabel(entry.date)}\n${entry.body || "하루한컷으로 남긴 기록"}`;
  if (navigator.share) await navigator.share({ title: "하루한컷", text }).catch(() => {});
  else if (navigator.clipboard) {
    await navigator.clipboard.writeText(text).catch(() => {});
    window.alert("기록 내용을 클립보드에 복사했어요.");
  }
}

function exportData() {
  const blob = new Blob([JSON.stringify({ exportedAt: new Date().toISOString(), entries }, null, 2)], { type: "application/json" });
  const link = document.createElement("a");
  link.href = URL.createObjectURL(blob);
  link.download = `dayframe-backup-${iso(new Date())}.json`;
  link.click();
  URL.revokeObjectURL(link.href);
}

function importData(file) {
  if (!file) return;
  const reader = new FileReader();
  reader.onload = () => {
    try {
      const parsed = JSON.parse(reader.result);
      if (!Array.isArray(parsed.entries)) throw new Error("invalid");
      entries = parsed.entries.filter((entry) => entry.date && entry.id);
      persist();
      render();
      window.alert(`${entries.length}개의 기록을 불러왔어요.`);
    } catch {
      window.alert("올바른 하루한컷 JSON 백업 파일이 아니에요.");
    }
  };
  reader.readAsText(file);
}

document.addEventListener("click", (event) => {
  const target = event.target.closest("[data-action], [data-view], [data-entry-id]");
  if (!target) return;
  const action = target.dataset.action;
  const view = target.dataset.view;
  const id = target.dataset.entryId;

  if (view) {
    state.view = view;
    render();
    return;
  }
  if (action === "new-entry") return openEditor();
  if (action === "previous-month") { state.month = new Date(state.month.getFullYear(), state.month.getMonth() - 1, 1); return render(); }
  if (action === "next-month") { state.month = new Date(state.month.getFullYear(), state.month.getMonth() + 1, 1); return render(); }
  if (action === "today") { const now = new Date(); state.month = new Date(now.getFullYear(), now.getMonth(), 1); return render(); }
  if (action === "create-for-date") return openEditor(null, target.dataset.date);
  if (action === "toggle-theme") {
    const isDark = document.body.dataset.theme === "dark";
    if (isDark) { document.body.removeAttribute("data-theme"); localStorage.setItem(THEME_KEY, "light"); }
    else { document.body.dataset.theme = "dark"; localStorage.setItem(THEME_KEY, "dark"); }
    return;
  }
  if (action === "sort-feed") { state.sortAsc = !state.sortAsc; return render(); }
  if (action === "close-modal") {
    if (target.classList.contains("modal-backdrop") && event.target !== target) return;
    return closeModal();
  }
  if (action === "choose-photo") return photoInput.click();
  if (action === "toggle-favorite") return toggleFavorite(id);
  if (action === "edit-entry") return openEditor(id);
  if (action === "delete-entry") return deleteEntry(id);
  if (action === "share-entry") return shareEntry(id);
  if (action === "export-data") return exportData();
  if (action === "import-data") return importInput.click();
  if (id && !target.closest(".modal")) return openDetail(id);
});

document.addEventListener("input", (event) => {
  if (event.target === searchInput) {
    state.query = event.target.value;
    render();
    return;
  }
  if (!state.editorDraft || !event.target.closest("#entry-form")) return;
  const field = event.target.name;
  if (field) state.editorDraft[field] = event.target.value;
});

document.addEventListener("change", (event) => {
  if (event.target === photoInput) {
    const file = event.target.files?.[0];
    if (!file || !state.editorDraft) return;
    const reader = new FileReader();
    reader.onload = () => { state.editorDraft.image = reader.result; renderEditorModal(); };
    reader.readAsDataURL(file);
    event.target.value = "";
    return;
  }
  if (event.target === importInput) {
    importData(event.target.files?.[0]);
    event.target.value = "";
  }
  if (state.editorDraft && event.target.closest("#entry-form")) {
    const field = event.target.name;
    if (field) state.editorDraft[field] = event.target.value;
  }
});

document.addEventListener("submit", (event) => {
  if (event.target.id !== "entry-form") return;
  event.preventDefault();
  saveDraft();
});

if ("serviceWorker" in navigator && location.protocol.startsWith("http")) {
  navigator.serviceWorker.register("./sw.js").catch(() => {});
}

render();
