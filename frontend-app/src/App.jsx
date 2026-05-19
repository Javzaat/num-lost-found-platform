import { useEffect, useState } from "react";
import axios from "axios";
import "./App.css";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

const LOCATION_OPTIONS = [
  "МУИС Номын сан",
  "Хичээлийн 1-р байр",
  "Хичээлийн 2-р байр",
  "Хичээлийн 4-р байр",
  "Хичээлийн 5-р байр",
  "Хичээлийн 7-р байр",
  "Хичээлийн 8-р байр",
  "Улаанбаатар Их Сургуулийн байр",
  "Оюутны байр",
  "Спорт заал",
  "Кафе / Цайны газар",
  "Гадна талбай",
  "Бусад",
];

const CATEGORY_OPTIONS = [
  "Түрийвч",
  "Оюутны үнэмлэх",
  "Утас",
  "Цүнх",
  "Ном / Дэвтэр",
  "Чихэвч",
  "Түлхүүр",
  "Карт",
  "Зөөврийн компьютер",
  "Бусад",
];

function App() {
  const [currentUser, setCurrentUser] = useState(() => {
    const savedUser = localStorage.getItem("numLostFoundUser");
    return savedUser ? JSON.parse(savedUser) : null;
  });

  const [authMode, setAuthMode] = useState("login");
  const [authMessage, setAuthMessage] = useState("");

  const [loginForm, setLoginForm] = useState({
    email: "",
    password: "",
  });

  const [registerForm, setRegisterForm] = useState({
    fullName: "",
    email: "",
    password: "",
  });

  const [items, setItems] = useState([]);
  const [claims, setClaims] = useState([]);
  const [matches, setMatches] = useState([]);
  const [selectedLostId, setSelectedLostId] = useState("");
  const [message, setMessage] = useState("");
  const [uploadingImage, setUploadingImage] = useState(false);

  const emptyItemForm = {
    title: "",
    description: "",
    category: "Түрийвч",
    location: "МУИС Номын сан",
    type: "LOST",
    contactName: currentUser?.fullName || "",
    contactEmail: currentUser?.email || "",
    imageUrl: "",
  };

  const emptyClaimForm = {
    lostItemId: "",
    foundItemId: "",
    claimantName: currentUser?.fullName || "",
    claimantEmail: currentUser?.email || "",
    proofDescription: "",
  };

  const [itemForm, setItemForm] = useState(emptyItemForm);
  const [claimForm, setClaimForm] = useState(emptyClaimForm);

  const handleRegister = async (e) => {
    e.preventDefault();

    try {
      const res = await axios.post(`${API_BASE_URL}/api/auth/register`, registerForm);

      localStorage.setItem("numLostFoundUser", JSON.stringify(res.data));
      setCurrentUser(res.data);
      setAuthMessage("");

      setItemForm((prev) => ({
        ...prev,
        contactName: res.data.fullName,
        contactEmail: res.data.email,
      }));

      setClaimForm((prev) => ({
        ...prev,
        claimantName: res.data.fullName,
        claimantEmail: res.data.email,
      }));
    } catch (error) {
      console.error("Register failed:", error);
      setAuthMessage(
        error.response?.data?.message || "Бүртгэл үүсгэхэд алдаа гарлаа."
      );
    }
  };

  const handleLogin = async (e) => {
    e.preventDefault();

    try {
      const res = await axios.post(`${API_BASE_URL}/api/auth/login`, loginForm);

      localStorage.setItem("numLostFoundUser", JSON.stringify(res.data));
      setCurrentUser(res.data);
      setAuthMessage("");

      setItemForm((prev) => ({
        ...prev,
        contactName: res.data.fullName,
        contactEmail: res.data.email,
      }));

      setClaimForm((prev) => ({
        ...prev,
        claimantName: res.data.fullName,
        claimantEmail: res.data.email,
      }));
    } catch (error) {
      console.error("Login failed:", error);
      setAuthMessage(
        error.response?.data?.message || "Нэвтрэхэд алдаа гарлаа."
      );
    }
  };

  const logout = () => {
    localStorage.removeItem("numLostFoundUser");
    setCurrentUser(null);
    setLoginForm({ email: "", password: "" });
    setRegisterForm({ fullName: "", email: "", password: "" });
    setItems([]);
    setClaims([]);
    setMatches([]);
    setMessage("");
  };

  const loadItems = async () => {
    try {
      const res = await axios.get(`${API_BASE_URL}/api/items`);
      setItems(res.data);
    } catch (error) {
      console.error("Failed to load items:", error);
      setMessage("Эд зүйлсийн мэдээлэл ачааллахад алдаа гарлаа.");
    }
  };

  const loadClaims = async () => {
    try {
      const res = await axios.get(`${API_BASE_URL}/api/claims`);
      setClaims(res.data);
    } catch (error) {
      console.error("Failed to load claims:", error);
      setMessage("Claim хүсэлтүүдийг ачааллахад алдаа гарлаа.");
    }
  };

  const uploadImage = async (file) => {
    if (!file) return;

    const formData = new FormData();
    formData.append("file", file);

    try {
      setUploadingImage(true);

      const res = await axios.post(`${API_BASE_URL}/api/files/upload`, formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      });

      setItemForm((prev) => ({
        ...prev,
        imageUrl: res.data.url,
      }));

      setMessage("Зураг амжилттай upload хийгдлээ.");
    } catch (error) {
      console.error("Image upload failed:", error);
      setMessage("Зураг upload хийхэд алдаа гарлаа. PNG, JPG, WEBP зураг ашиглана уу.");
    } finally {
      setUploadingImage(false);
    }
  };

  const createItem = async (e) => {
    e.preventDefault();

    try {
      const res = await axios.post(`${API_BASE_URL}/api/items`, itemForm);
      console.log("Created item:", res.data);

      setMessage("Эд зүйл амжилттай бүртгэгдлээ.");

      setItemForm({
        title: "",
        description: "",
        category: "Түрийвч",
        location: "МУИС Номын сан",
        type: "LOST",
        contactName: currentUser?.fullName || "",
        contactEmail: currentUser?.email || "",
        imageUrl: "",
      });

      await loadItems();
    } catch (error) {
      console.error("Create item failed:", error);
      setMessage("Эд зүйл бүртгэхэд алдаа гарлаа.");
    }
  };

  const findMatches = async () => {
    if (!selectedLostId) {
      setMessage("Эхлээд алдагдсан эд зүйл сонгоно уу.");
      return;
    }

    try {
      const res = await axios.get(
        `${API_BASE_URL}/api/matches/lost/${selectedLostId}`
      );

      setMatches(res.data);
      setMessage(`${res.data.length} боломжит тохирол олдлоо.`);
    } catch (error) {
      console.error("Find matches failed:", error);
      setMessage("Тохирол хайхад алдаа гарлаа.");
    }
  };

  const createClaim = async (e) => {
    e.preventDefault();

    try {
      const res = await axios.post(`${API_BASE_URL}/api/claims`, {
        lostItemId: Number(claimForm.lostItemId),
        foundItemId: Number(claimForm.foundItemId),
        claimantName: claimForm.claimantName,
        claimantEmail: claimForm.claimantEmail,
        proofDescription: claimForm.proofDescription,
      });

      console.log("Created claim:", res.data);

      setMessage("Буцаан авах хүсэлт амжилттай илгээгдлээ.");

      setClaimForm({
        lostItemId: "",
        foundItemId: "",
        claimantName: currentUser?.fullName || "",
        claimantEmail: currentUser?.email || "",
        proofDescription: "",
      });

      await loadClaims();
    } catch (error) {
      console.error("Create claim failed:", error);
      setMessage("Буцаан авах хүсэлт илгээхэд алдаа гарлаа.");
    }
  };

  const updateClaimStatus = async (claimId, status) => {
    try {
      const res = await axios.patch(
        `${API_BASE_URL}/api/claims/${claimId}/status/${status}`
      );

      console.log("Updated claim:", res.data);

      if (status === "APPROVED") {
        await Promise.all([
          axios.patch(`${API_BASE_URL}/api/items/${res.data.lostItemId}/status/CLAIMED`),
          axios.patch(`${API_BASE_URL}/api/items/${res.data.foundItemId}/status/CLAIMED`),
        ]);

        setMessage("Claim батлагдаж, эд зүйлс CLAIMED төлөвтэй боллоо.");
      } else {
        setMessage("Claim хүсэлт татгалзагдлаа.");
      }

      await loadItems();
      await loadClaims();
    } catch (error) {
      console.error("Update claim failed:", error);
      setMessage("Claim status өөрчлөхөд алдаа гарлаа.");
    }
  };

  useEffect(() => {
    if (!currentUser) return;

    let ignore = false;

    const loadDashboardData = async () => {
      try {
        const [itemsRes, claimsRes] = await Promise.all([
          axios.get(`${API_BASE_URL}/api/items`),
          axios.get(`${API_BASE_URL}/api/claims`),
        ]);

        if (!ignore) {
          setItems(itemsRes.data);
          setClaims(claimsRes.data);
        }
      } catch (error) {
        console.error("Failed to load dashboard data:", error);

        if (!ignore) {
          setMessage("Dashboard мэдээлэл ачааллахад алдаа гарлаа.");
        }
      }
    };

    loadDashboardData();

    return () => {
      ignore = true;
    };
  }, [currentUser]);

  if (!currentUser) {
    return (
      <AuthPage
        authMode={authMode}
        setAuthMode={setAuthMode}
        loginForm={loginForm}
        setLoginForm={setLoginForm}
        registerForm={registerForm}
        setRegisterForm={setRegisterForm}
        handleLogin={handleLogin}
        handleRegister={handleRegister}
        authMessage={authMessage}
      />
    );
  }

  const lostItems = items.filter(
    (item) => item.type === "LOST" && item.status === "OPEN"
  );

  const foundItems = items.filter(
    (item) => item.type === "FOUND" && item.status === "OPEN"
  );

  return (
    <div className="app">
      <header className="hero">
        <div>
          <p className="eyebrow">Service-Oriented Architecture Project</p>
          <h1>МУИС Lost & Found үйлчилгээний платформ</h1>
          <p className="hero-text">
            Алдагдсан болон олдсон эд зүйл бүртгэх, зураг хавсаргах, боломжит
            тохирлыг автоматаар санал болгох, буцаан авах хүсэлтийг удирдах
            microservice суурьтай систем.
          </p>

          <div className="user-bar">
            <span>
              Нэвтэрсэн хэрэглэгч: <strong>{currentUser.fullName}</strong> ·{" "}
              {currentUser.email}
            </span>
            <button type="button" className="logout-button" onClick={logout}>
              Гарах
            </button>
          </div>
        </div>

        <div className="hero-card">
          <h3>Ажиллаж буй сервисүүд</h3>
          <p>Auth Service: 8081</p>
          <p>API Gateway: 8080</p>
          <p>Item Service: 8082</p>
          <p>Matching Service: 8083</p>
          <p>Claim Service: 8084</p>
          <p>File Service: 8085</p>
        </div>
      </header>

      {message && <div className="message">{message}</div>}

      <main className="grid">
        <section className="card">
          <h2>Алдагдсан / Олдсон эд зүйл бүртгэх</h2>

          <form onSubmit={createItem} className="form">
            <input
              placeholder="Гарчиг"
              value={itemForm.title}
              onChange={(e) =>
                setItemForm({ ...itemForm, title: e.target.value })
              }
              required
            />

            <textarea
              placeholder="Тайлбар"
              value={itemForm.description}
              onChange={(e) =>
                setItemForm({ ...itemForm, description: e.target.value })
              }
              required
            />

            <select
              value={itemForm.category}
              onChange={(e) =>
                setItemForm({ ...itemForm, category: e.target.value })
              }
              required
            >
              {CATEGORY_OPTIONS.map((category) => (
                <option key={category} value={category}>
                  {category}
                </option>
              ))}
            </select>

            <select
              value={itemForm.location}
              onChange={(e) =>
                setItemForm({ ...itemForm, location: e.target.value })
              }
              required
            >
              {LOCATION_OPTIONS.map((location) => (
                <option key={location} value={location}>
                  {location}
                </option>
              ))}
            </select>

            <select
              value={itemForm.type}
              onChange={(e) =>
                setItemForm({ ...itemForm, type: e.target.value })
              }
            >
              <option value="LOST">Алдагдсан эд зүйл</option>
              <option value="FOUND">Олдсон эд зүйл</option>
            </select>

            <input
              placeholder="Холбогдох хүний нэр"
              value={itemForm.contactName}
              onChange={(e) =>
                setItemForm({ ...itemForm, contactName: e.target.value })
              }
              required
            />

            <input
              placeholder="Имэйл хаяг"
              type="email"
              value={itemForm.contactEmail}
              onChange={(e) =>
                setItemForm({ ...itemForm, contactEmail: e.target.value })
              }
              required
            />

            <label className="file-label">
              Эд зүйлийн зураг upload хийх
              <input
                type="file"
                accept="image/png,image/jpeg,image/webp"
                onChange={(e) => uploadImage(e.target.files[0])}
              />
            </label>

            {uploadingImage && <p className="small-text">Зураг upload хийж байна...</p>}

            {itemForm.imageUrl && (
              <div className="preview-wrapper">
                <img
                  src={`${API_BASE_URL}${itemForm.imageUrl}`}
                  alt="Preview"
                  className="preview-image"
                />
                <p className="small-text">Зураг амжилттай хавсарсан.</p>
              </div>
            )}

            <button type="submit" disabled={uploadingImage}>
              {uploadingImage ? "Зураг upload хийж байна..." : "Эд зүйл бүртгэх"}
            </button>
          </form>
        </section>

        <section className="card">
          <h2>Ухаалаг тохирол хайх</h2>

          <select
            value={selectedLostId}
            onChange={(e) => setSelectedLostId(e.target.value)}
          >
            <option value="">Алдагдсан эд зүйл сонгох</option>
            {lostItems.map((item) => (
              <option key={item.id} value={item.id}>
                #{item.id} - {item.title}
              </option>
            ))}
          </select>

          <button type="button" onClick={findMatches}>
            Тохирол хайх
          </button>

          <div className="list">
            {matches.length === 0 && (
              <p className="empty-text">Одоогоор тохирол хайгаагүй байна.</p>
            )}

            {matches.map((match) => (
              <div
                key={`${match.lostItemId}-${match.foundItemId}`}
                className="mini-card"
              >
                <h3>{match.foundItemTitle}</h3>
                <p>
                  <strong>Алдагдсан:</strong> #{match.lostItemId}{" "}
                  {match.lostItemTitle}
                </p>
                <p>
                  <strong>Олдсон:</strong> #{match.foundItemId}{" "}
                  {match.foundItemTitle}
                </p>
                <p>
                  <strong>Тохирлын оноо:</strong> {match.score}
                </p>
                <p>{match.reason}</p>
              </div>
            ))}
          </div>
        </section>

        <section className="card wide">
          <h2>Бүртгэгдсэн эд зүйлс</h2>

          <div className="columns">
            <div>
              <h3>Алдагдсан эд зүйлс</h3>
              {lostItems.length === 0 && (
                <p className="empty-text">Алдагдсан эд зүйл бүртгэгдээгүй байна.</p>
              )}
              {lostItems.map((item) => (
                <ItemCard key={item.id} item={item} />
              ))}
            </div>

            <div>
              <h3>Олдсон эд зүйлс</h3>
              {foundItems.length === 0 && (
                <p className="empty-text">Олдсон эд зүйл бүртгэгдээгүй байна.</p>
              )}
              {foundItems.map((item) => (
                <ItemCard key={item.id} item={item} />
              ))}
            </div>
          </div>
        </section>

        <section className="card">
          <h2>Буцаан авах хүсэлт үүсгэх</h2>

          <form onSubmit={createClaim} className="form">
            <select
              value={claimForm.lostItemId}
              onChange={(e) =>
                setClaimForm({ ...claimForm, lostItemId: e.target.value })
              }
              required
            >
              <option value="">Алдагдсан эд зүйл сонгох</option>
              {lostItems.map((item) => (
                <option key={item.id} value={item.id}>
                  #{item.id} - {item.title}
                </option>
              ))}
            </select>

            <select
              value={claimForm.foundItemId}
              onChange={(e) =>
                setClaimForm({ ...claimForm, foundItemId: e.target.value })
              }
              required
            >
              <option value="">Олдсон эд зүйл сонгох</option>
              {foundItems.map((item) => (
                <option key={item.id} value={item.id}>
                  #{item.id} - {item.title}
                </option>
              ))}
            </select>

            <input
              placeholder="Хүсэлт гаргагчийн нэр"
              value={claimForm.claimantName}
              onChange={(e) =>
                setClaimForm({ ...claimForm, claimantName: e.target.value })
              }
              required
            />

            <input
              placeholder="Имэйл хаяг"
              type="email"
              value={claimForm.claimantEmail}
              onChange={(e) =>
                setClaimForm({ ...claimForm, claimantEmail: e.target.value })
              }
              required
            />

            <textarea
              placeholder="Нотлох тайлбар"
              value={claimForm.proofDescription}
              onChange={(e) =>
                setClaimForm({
                  ...claimForm,
                  proofDescription: e.target.value,
                })
              }
              required
            />

            <button type="submit">Хүсэлт илгээх</button>
          </form>
        </section>

        <section className="card">
          <h2>Буцаан авах хүсэлтүүд</h2>

          <div className="list">
            {claims.length === 0 && (
              <p className="empty-text">Одоогоор хүсэлт байхгүй байна.</p>
            )}

            {claims.map((claim) => (
              <div key={claim.id} className="mini-card">
                <h3>Claim #{claim.id}</h3>
                <p>
                  Алдагдсан эд зүйл #{claim.lostItemId} → Олдсон эд зүйл #
                  {claim.foundItemId}
                </p>
                <p>
                  <strong>Хүсэлт гаргагч:</strong> {claim.claimantName}
                </p>
                <p>
                  <strong>Имэйл:</strong> {claim.claimantEmail}
                </p>
                <p>
                  <strong>Нотлох тайлбар:</strong> {claim.proofDescription}
                </p>
                <p>
                  <strong>Төлөв:</strong>{" "}
                  <span className={`status ${claim.status.toLowerCase()}`}>
                    {translateClaimStatus(claim.status)}
                  </span>
                </p>

                {claim.status === "PENDING" ? (
                  <div className="actions">
                    <button
                      type="button"
                      onClick={() => updateClaimStatus(claim.id, "APPROVED")}
                    >
                      Батлах
                    </button>
                    <button
                      type="button"
                      className="danger"
                      onClick={() => updateClaimStatus(claim.id, "REJECTED")}
                    >
                      Татгалзах
                    </button>
                  </div>
                ) : (
                  <p className="resolved-text">
                    Энэ хүсэлт шийдвэрлэгдсэн байна.
                  </p>
                )}
              </div>
            ))}
          </div>
        </section>
      </main>
    </div>
  );
}

function AuthPage({
  authMode,
  setAuthMode,
  loginForm,
  setLoginForm,
  registerForm,
  setRegisterForm,
  handleLogin,
  handleRegister,
  authMessage,
}) {
  return (
    <div className="auth-page">
      <div className="auth-panel">
        <p className="eyebrow">NUM Lost & Found</p>
        <h1>Системд нэвтрэх</h1>
        <p>
          Dashboard ашиглахын тулд эхлээд нэвтэрнэ үү. Бүртгэлгүй хэрэглэгч
          шууд dashboard харах боломжгүй.
        </p>

        <div className="auth-tabs">
          <button
            type="button"
            className={authMode === "login" ? "active" : ""}
            onClick={() => setAuthMode("login")}
          >
            Нэвтрэх
          </button>
          <button
            type="button"
            className={authMode === "register" ? "active" : ""}
            onClick={() => setAuthMode("register")}
          >
            Бүртгүүлэх
          </button>
        </div>

        {authMessage && <div className="auth-message">{authMessage}</div>}

        {authMode === "login" ? (
          <form onSubmit={handleLogin} className="form">
            <input
              type="email"
              placeholder="Имэйл хаяг"
              value={loginForm.email}
              onChange={(e) =>
                setLoginForm({ ...loginForm, email: e.target.value })
              }
              required
            />

            <input
              type="password"
              placeholder="Нууц үг"
              value={loginForm.password}
              onChange={(e) =>
                setLoginForm({ ...loginForm, password: e.target.value })
              }
              required
            />

            <button type="submit">Нэвтрэх</button>
          </form>
        ) : (
          <form onSubmit={handleRegister} className="form">
            <input
              placeholder="Овог нэр"
              value={registerForm.fullName}
              onChange={(e) =>
                setRegisterForm({ ...registerForm, fullName: e.target.value })
              }
              required
            />

            <input
              type="email"
              placeholder="Имэйл хаяг"
              value={registerForm.email}
              onChange={(e) =>
                setRegisterForm({ ...registerForm, email: e.target.value })
              }
              required
            />

            <input
              type="password"
              placeholder="Нууц үг"
              value={registerForm.password}
              onChange={(e) =>
                setRegisterForm({ ...registerForm, password: e.target.value })
              }
              required
            />

            <button type="submit">Бүртгүүлэх</button>
          </form>
        )}
      </div>

      <div className="auth-info">
        <h2>SOA Project</h2>
        <p>
          Энэхүү систем нь Auth Service, API Gateway, Item Service, Matching
          Service, Claim Service, File Service гэсэн тусдаа service-үүдээс
          бүрдэнэ.
        </p>
        <ul>
          <li>Login/Register authentication flow</li>
          <li>Dashboard route protection</li>
          <li>Lost/Found item photo upload</li>
          <li>Smart matching and claim workflow</li>
        </ul>
      </div>
    </div>
  );
}

function ItemCard({ item }) {
  return (
    <div className="mini-card">
      <h3>
        #{item.id} {item.title}
      </h3>

      {item.imageUrl && (
        <img
          src={`${API_BASE_URL}${item.imageUrl}`}
          alt={item.title}
          className="item-image"
        />
      )}

      <p>{item.description}</p>
      <p>
        <strong>Төрөл:</strong> {translateItemType(item.type)}
      </p>
      <p>
        <strong>Ангилал:</strong> {item.category}
      </p>
      <p>
        <strong>Байршил:</strong> {item.location}
      </p>
      <p>
        <strong>Холбогдох хүн:</strong> {item.contactName}
      </p>
      <p>
        <strong>Имэйл:</strong> {item.contactEmail}
      </p>
      <p>
        <strong>Төлөв:</strong>{" "}
        <span className={`status ${item.status.toLowerCase()}`}>
          {translateItemStatus(item.status)}
        </span>
      </p>
    </div>
  );
}

function translateItemType(type) {
  if (type === "LOST") return "Алдагдсан";
  if (type === "FOUND") return "Олдсон";
  return type;
}

function translateItemStatus(status) {
  if (status === "OPEN") return "Нээлттэй";
  if (status === "MATCHED") return "Тохирол олдсон";
  if (status === "CLAIMED") return "Буцаагдсан";
  if (status === "CLOSED") return "Хаагдсан";
  return status;
}

function translateClaimStatus(status) {
  if (status === "PENDING") return "Хүлээгдэж байна";
  if (status === "APPROVED") return "Батлагдсан";
  if (status === "REJECTED") return "Татгалзсан";
  return status;
}

export default App;
